package com.billsAplication.presentation.billsList

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.billsAplication.BillsApplication
import com.billsAplication.R
import com.billsAplication.databinding.FragmentBillsListBinding
import com.billsAplication.domain.model.BillsItem
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_CATEGORY_EXPENSES
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_CATEGORY_INCOME
import com.billsAplication.domain.model.BillsItem.Companion.TYPE_EQUALS
import com.billsAplication.extension.DATE_FORMAT
import com.billsAplication.presentation.analytics.AnalyticsDialog
import com.billsAplication.presentation.billsList.view.BillsItemList
import com.billsAplication.presentation.billsList.view.DateItem
import com.billsAplication.presentation.billsList.view.TotalAmountItem
import com.billsAplication.presentation.bookmarks.BookmarksDialog
import com.billsAplication.presentation.components.AddButton
import com.billsAplication.presentation.components.AmountBar
import com.billsAplication.presentation.components.ImgBtn
import com.billsAplication.presentation.components.MonthPicker
import com.billsAplication.presentation.createBillDialog.AddBillDialog
import com.billsAplication.presentation.createBillDialog.CreateBillDialog
import com.billsAplication.presentation.createBillDialog.createBill.ConfirmationDialog
import com.billsAplication.utils.InterfaceMainActivity
import com.billsAplication.utils.PagingConstants.DP_10
import com.billsAplication.utils.PagingConstants.DP_5
import com.billsAplication.utils.StateColorButton
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class BillsListFragment : Fragment() {

    private var _binding: FragmentBillsListBinding? = null
    private val binding: FragmentBillsListBinding get() = _binding!!
    private var _listDeleteItems: ArrayList<BillsItem> = ArrayList()


    @Inject
    lateinit var viewModel: BillsListViewModel

    private val exception = CoroutineExceptionHandler { _, e ->
        Log.e(TAG, "BillsListFragment:: ${e.message!!}: ", e)
    }

    private val mainActivity by lazy {
        (context as InterfaceMainActivity)
    }

    private val component by lazy {
        (requireActivity().application as BillsApplication).component
    }

    companion object {
        private const val TYPE_NOTE_RECEIVE = "type_note_receive"
        private const val TYPE_BILL = "type_bill"
        private const val TAG = "BillsListFragment"
        private const val FILTER_FULL_LIST = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO
        initRecView()
        //TODO
        onBackPressed()

        binding.composeView.setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    BillsListScreen()
                }
            }
        }
        //TODO
        //Get off splash
        lifecycleScope.launch(exception) {
            delay(500)
            if (requireActivity() is InterfaceMainActivity) {
                mainActivity.splash()
            }
            //if we receive note string from other app
            intentActionSendText()
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    private fun BillsListScreen() {
        val listDeleteItems = remember { mutableStateListOf(listOf<BillsItem>()) }
        val incAmountState = remember { mutableStateOf("0.0") }
        val expAmountState = remember { mutableStateOf("0.0") }
        val totalAmountState = remember { mutableStateOf("0.0") }
        val typeColorAddButton = remember { mutableIntStateOf(TYPE_EQUALS) }
        val typeIconAddButton = remember { mutableIntStateOf(R.drawable.ic_add) }
        val month = remember { mutableStateOf("") }
        val showDialogDelete = remember { mutableStateOf(false) }
        val showDialogAddBill = remember { mutableStateOf(CreateBillDialog(show = false, bill = null)) }
        val showBookmarksDialog = remember { mutableStateOf(false) }
        val showAnalyticsDialog = remember { mutableStateOf(false) }
        val isSortDescending = remember { mutableStateOf(true) }
        val filterState = remember { mutableIntStateOf(FILTER_FULL_LIST) }
        
        val bills = viewModel.getMonthListFlow(month = month.value).collectAsState(initial = listOf())

        val filteredBills = remember(bills.value, filterState.intValue) {
            if (filterState.intValue == FILTER_FULL_LIST) {
                bills.value
            } else {
                bills.value.filter { it.type == filterState.intValue }
            }
        }

        /**
         * Create dialog for delete Items
         * */
        ConfirmDeleteDialog(
            showDialogDelete = showDialogDelete,
            listDeleteItems = listDeleteItems,
            typeIconAddButton = typeIconAddButton
        )
        /**
         * Create dialog for add Items
         * */
        AddBill(
            showDialogAddBill = showDialogAddBill
        )

        /**
         * Bookmarks dialog
         */
        val bookmarks = viewModel.bookmarks.observeAsState(initial = listOf())
        BookmarksDialog(
            showDialog = showBookmarksDialog,
            bookmarks = bookmarks.value,
            onBookmarkClick = { bookmark ->
                showBookmarksDialog.value = false
                showDialogAddBill.value = CreateBillDialog(show = true, bill = bookmark)
            },
            onDeleteBookmarks = { itemsToDelete ->
                itemsToDelete.forEach { item ->
                    viewModel.updateBookmark(item.copy(bookmark = false))
                }
            },
            onDismiss = {}
        )
        
        /**
         * Analytics dialog
         */
        AnalyticsDialog(
            showDialog = showAnalyticsDialog,
            bills = bills.value,
            onDismiss = {}
        )

        /**
         * Income amount, expense amount, total amount, typeColorAddButton
         * */
        viewModel.summaryAmount(month.value) { inc, exp, total, typeColor ->
            incAmountState.value = inc
            expAmountState.value = exp
            totalAmountState.value = total
            typeColorAddButton.intValue = typeColor
            setNavButtonColor(typeColor)
            StateColorButton.CURRENT_TYPE = typeColor
        }

        TopBar(
            month = month, 
            onBookmarksClick = { showBookmarksDialog.value = true },
            onAnalyticsClick = { showAnalyticsDialog.value = true }
        )

        AmountBar(
            incAmountState = incAmountState,
            expAmountState = expAmountState,
            totalAmountState = totalAmountState,
            isSortDescending = isSortDescending,
            filterState = filterState
        )

        Divider(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .width(1.dp)
                .padding(bottom = DP_5)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = DP_5,
                        bottomEnd = DP_5,
                    ),
                )
        )

        Box {
            BillsList(
                bills = filteredBills.toMapByDate(),
                listDeleteItems = listDeleteItems,
                typeIconAddButton = typeIconAddButton,
                showDialogAddBill = showDialogAddBill,
                isSortDescending = isSortDescending.value
            )

            AddButton(
                painterResource = typeIconAddButton,
                typeColorAddButton = typeColorAddButton,
                application = (requireActivity().application as BillsApplication),
                onCLick = {
                    val list = getListDeleteItems(listDeleteItems = listDeleteItems)

                    if (list.isNotEmpty()) {
                        showDialogDelete.value = true
                    } else {
                        showDialogAddBill.value = CreateBillDialog(show = true, bill = null)
                    }
                })
        }
    }

    @Composable
    fun TopBar(
        month: MutableState<String>,
        onBookmarksClick: () -> Unit,
        onAnalyticsClick: () -> Unit
    ) {
        Row(
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(DP_10)
                .fillMaxWidth()
        ) {
            MonthPicker(month = month)

            Row(
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = DP_10)
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth(.4f)
                )

                ImgBtn(
                    src = painterResource(id = R.drawable.ic_bookmarks),
                    description = stringResource(id = R.string.description_bookmarks)
                ) {
                    onBookmarksClick()
                }

                ImgBtn(
                    src = painterResource(id = R.drawable.ic_analytics),
                    description = stringResource(id = R.string.menu_analytics)
                ) {
                    onAnalyticsClick()
                }

                ImgBtn(
                    src = painterResource(id = R.drawable.ic_search),
                    description = stringResource(id = R.string.description_search)
                ) {
                    findNavController().navigate(
                        R.id.action_billsListFragment_to_searchFragment
                    )
                }
            }
        }
    }

    @Composable
    private fun BillsList(
        bills: Map<String, List<BillsItem>>,
        listDeleteItems: SnapshotStateList<List<BillsItem>>,
        typeIconAddButton: MutableIntState,
        showDialogAddBill: MutableState<CreateBillDialog>,
        isSortDescending: Boolean
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(start = DP_10, end = DP_10)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)

            val sortedBills = bills.filterKeys { it.isNotEmpty() }.toSortedMap(
                if (isSortDescending) {
                    compareByDescending { dateString -> LocalDate.parse(dateString, formatter) }
                } else {
                    compareBy { dateString -> LocalDate.parse(dateString, formatter) }
                }
            )

            sortedBills.forEach { (date, items) ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(DP_10),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = DP_10
                        ),
                        shape = RoundedCornerShape(DP_10 + DP_5)
                    ) {
                        Column(
                            modifier = Modifier.padding(DP_10 + DP_5)
                        ) {
                            DateItem(date = date)

                            Spacer(modifier = Modifier.height(DP_5))

                            SetBillsItems(
                                items = items,
                                listDeleteItems = listDeleteItems,
                                typeIconAddButton = typeIconAddButton,
                                showDialogAddBill = showDialogAddBill
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AddBill(
        showDialogAddBill: MutableState<CreateBillDialog>
    ) {
        AddBillDialog(
            viewModel = viewModel,
            showDialog = showDialogAddBill,
            onDismiss = {}
        )
    }

    private var countAmount = 0F

    @Composable
    private fun SetBillsItems(
        items: List<BillsItem>,
        listDeleteItems: SnapshotStateList<List<BillsItem>>,
        typeIconAddButton: MutableIntState,
        showDialogAddBill: MutableState<CreateBillDialog>
    ) {
        items.forEachIndexed { index, item ->
            BillsItemList(
                listDeleteItems = listDeleteItems,
                item = item,
                onClick = {  bill ->
                    val list = getListDeleteItems(listDeleteItems = listDeleteItems)
                    if (list.isNotEmpty()) {
                        addToDeleteList(
                            bill = bill,
                            listDeleteItems = listDeleteItems,
                            list = list,
                            typeIconAddButton = typeIconAddButton
                        )
                    } else {
                        showDialogAddBill.value = CreateBillDialog(show = true, bill = bill)
                    }
                },
                onLongClick = { bill ->
                    val list = getListDeleteItems(listDeleteItems = listDeleteItems)
                    addToDeleteList(
                        bill = bill,
                        listDeleteItems = listDeleteItems,
                        list = list,
                        typeIconAddButton = typeIconAddButton
                    )
                }
            )

            SetTotalAmount(item = item, index = index, items = items)
        }
    }

    @Composable
    fun SetTotalAmount(item: BillsItem, index: Int, items: List<BillsItem>) {
        try {
            when (item.type) {
                BillsItem.TYPE_EXPENSES ->
                    countAmount -= item.amount.replace(",", "").toFloat()

                BillsItem.TYPE_INCOME ->
                    countAmount += item.amount.replace(",", "").toFloat()
            }
        } catch (e: Exception) {
            Log.e(TAG, "SetTotalAmount: ", e)
        }


        if (
            index == items.lastIndex ||
            items.lastIndex >= index + 1 &&
            item.date != items[index + 1].date
        ) {
            TotalAmountItem(
                bigDp = DP_10,
                smallDp = DP_5,
                amount = "%,.2f".format(countAmount)
            )
            countAmount = 0f
        }
    }

    @Composable
    private fun ConfirmDeleteDialog(
        showDialogDelete: MutableState<Boolean>,
        listDeleteItems: SnapshotStateList<List<BillsItem>>,
        typeIconAddButton: MutableIntState,
    ) {
        ConfirmationDialog(
            title = stringResource(id = R.string.dialog_title_delete_Bills),
            message = stringResource(id = R.string.dialog_message_delete_bills),
            confirmButtonText = stringResource(id = R.string.button_yes),
            cancelButtonText = stringResource(id = R.string.search_cancel),
            showDialog = showDialogDelete,
            onDeleteConfirmed = {
                val list = getListDeleteItems(listDeleteItems = listDeleteItems)

                if (list.isNotEmpty()) {
                    list.forEach {
                        viewModel.delete(it)
                    }
                    setListDeleteItems(
                        listDeleteItems = listDeleteItems,
                        list = listOf(),
                        typeIconAddButton = typeIconAddButton
                    )
                }
            }
        )
    }

    private fun getListDeleteItems(
        listDeleteItems: SnapshotStateList<List<BillsItem>>
    ) = if (listDeleteItems.isNotEmpty()) listDeleteItems.first().toMutableList() else mutableListOf()

    private fun setListDeleteItems(
        typeIconAddButton: MutableIntState,
        listDeleteItems: SnapshotStateList<List<BillsItem>>,
        list: List<BillsItem>,
    ) {
        listDeleteItems.clear()
        listDeleteItems.addAll(listOf(list))
        if (list.isEmpty()) {
            typeIconAddButton.intValue = R.drawable.ic_add
        } else {
            typeIconAddButton.intValue = R.drawable.ic_delete_forever
        }
    }

    private fun addToDeleteList(
        bill: BillsItem,
        listDeleteItems: SnapshotStateList<List<BillsItem>>,
        list: MutableList<BillsItem>,
        typeIconAddButton: MutableIntState
    ) {
        if (list.contains(bill)) {
            list.remove(bill)
        } else {
            list.add(bill)
        }
        setListDeleteItems(
            listDeleteItems = listDeleteItems,
            list = list,
            typeIconAddButton = typeIconAddButton
        )
    }

    private fun List<BillsItem>.toMapByDate(): Map<String, List<BillsItem>> {
        return this.groupBy { it.date }
    }

    private fun intentActionSendText() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val typeAction = sharedPref.getBoolean(TYPE_NOTE_RECEIVE, false)
        if (typeAction) {
            if (requireActivity() is InterfaceMainActivity) {
                mainActivity.navBottom().selectedItemId = R.id.shopListFragment
            }
        }
    }

    @SuppressLint("ResourceType", "CutPasteId", "UseCompatLoadingForDrawables")
    private fun setNavButtonColor(type: Int) {
        with(StateColorButton(application = (requireActivity().application as BillsApplication))) {
            if (requireActivity() is InterfaceMainActivity) {
                with(mainActivity.navBottom()) {
                    stateNavBot(type).let {
                        if (itemIconTintList != it) { //if do it again
                            itemIconTintList = it //set color of icon nav bottom income
                            itemTextColor = it //set color of text nav bottom income
                            itemRippleColor = it //set color effect
                        }
                    }
                }
            }
            setSharePrefColors(type) //TODO Убрать
        }
    }


    private fun setSharePrefColors(type: Int) {
        val sharedPref = requireActivity().getPreferences(AppCompatActivity.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(TYPE_BILL, type)
            apply()
        }
    }

    @SuppressLint("ResourceType")
    private fun initRecView() {

//        billAdapter.onClickListenerBillItem = {
//            bundle.putInt(ADD_BILL_KEY, UPDATE_TYPE)
//            bundle.putParcelable(BILL_ITEM_KEY, it)
//            findNavController().navigate(R.id.action_billsListFragment_to_addBillFragment, bundle)
//        }

//        billAdapter.onLongClickListenerBillItem = {
//            listDeleteItems = it
//        }
        //Highlight item
//        billAdapter.isHighlight.observe(viewLifecycleOwner) {
//            deleteItem = it
//            if (it) { //GONE VIEWS
//                deleteItem = it
//                //AddButton
//
//                //NavBottom
//                if (requireActivity() is InterfaceMainActivity) {
//                    mainActivity.navBottom().visibility = View.INVISIBLE
//                }
//            }
//        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                @SuppressLint("NotifyDataSetChanged")
                override fun handleOnBackPressed() {
                    if (_listDeleteItems.isEmpty()) {
                        requireActivity().finishAffinity()
                        requireActivity().finish()
//                        isEnabled = false
//                        requireActivity().onBackPressed()
                    } else { //TODO Убрать Крест
                        _listDeleteItems.clear()
//                        billAdapter.deleteItemsAfterRemovedItemFromDB()
//                        billAdapter.notifyDataSetChanged()
                    }
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        mainActivity.navBottom().visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}