package com.nuhlp.nursehelper.ui.home

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.nuhlp.nursehelper.utill.base.map.BaseMapFragment
import com.nuhlp.nursehelper.R
import com.nuhlp.nursehelper.databinding.FragmentHomeBinding
import com.nuhlp.nursehelper.datasource.network.model.place.Place
import com.nuhlp.nursehelper.datasource.room.app.*
import com.nuhlp.nursehelper.utill.test.DummyDataUtil
import com.nuhlp.nursehelper.utill.useapp.AppTime
import com.nuhlp.nursehelper.utill.useapp.DocListAdapter
import com.nuhlp.nursehelper.utill.useapp.MarginItemDecoration
import com.nuhlp.nursehelper.utill.adapter.PatientsListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

// ** 인덱스 어답터 베이스 프래그먼트로 빼기 **
// 최대한 xml에 livedata를 이용하게 바꾸기
// repository retrofit 사용 가능하게 바꾸기
// appDB에 place 저장할 테이블 생성 (place에서 사용하는 값만 파라미터로 가지고있는)
// 테이블에 place 초기 데이터(카카오 place) 주입
// 환자 더미 데이터 place 번호 사용하게 생성
// 지도 추가해서 내위치 근처 병원 출력
// 환자목록 리클라이어뷰 추가
// 가장 가까운 병원으로 환자검색후 환자목록 리클라이어뷰 어답터에 주입
// 가장 첫번째 환자 검색후 기존 뷰모델 환자 라이브에 주입

class HomeFragment : BaseMapFragment<FragmentHomeBinding>(),HomeUtil {

    override val layoutResourceId = R.layout.fragment_home
    override val markerResourceId= R.drawable.ic_hospital_marker

    private lateinit var _liveDocAdapter: DocListAdapter
    private lateinit var _livePatAdapter: PatientsListAdapter

    override val mapViewModel by lazy { _homeViewModel }

    private val _homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(
            this,
            HomeViewModel.Factory()
        ).get(HomeViewModel::class.java)
    }

    override fun onCreateViewAfterMap() {
        binding.viewModel = _homeViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.mapUtil = this
        binding.homeUtil = this
        if(isMapReady()){
            updateLocation()
            Log.d("HomeFragment","onCreate!")
        }

        tempSettings()
    }

    private fun tempSettings() {
        binding.placeCardView.setOnClickListener {
            dummyInit()
            Toast.makeText(context,"더미데이터 생성",Toast.LENGTH_SHORT).show()
        }
    }


    override fun onUpdateMyLatLng(latLng: LatLng) {
        _homeViewModel.updatePlaces(latLng)
        Log.d("HomeFragment","onUpdateMyLatLng()")
    }

    override fun setPatientRecyclerView(view:RecyclerView) {
        view.apply {
            // ** layoutManager **
            layoutManager = LinearLayoutManager(
                this@HomeFragment.context,
                LinearLayoutManager.HORIZONTAL, false
            )

            // ** Adapter **
            _livePatAdapter = PatientsListAdapter { patient ->
                _homeViewModel.updatePatient(patient)
            }
            adapter = _livePatAdapter

            // ** deco **
            val mid =  MarginItemDecoration(7)
            addItemDecoration(mid)
            itemAnimator = null
        }
    }
    override fun setDocumentRecyclerView(view:RecyclerView) {
        view.apply {

        // ** layoutManager **
        layoutManager = LinearLayoutManager(
            this@HomeFragment.context,
            LinearLayoutManager.HORIZONTAL, false
        )

        // ** Adapter **
         _liveDocAdapter = DocListAdapter {
            val docNO = it.docNo
            val action = HomeFragmentDirections.actionHomeFragmentToProgressReportFragment(docNO, emptyList<String>().toTypedArray()
            )
           this.findNavController().navigate(action)
        }
        adapter = _liveDocAdapter

        // ** deco **
        val mid =  MarginItemDecoration(7)
        addItemDecoration(mid)
        itemAnimator = null

        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val tagPlace = marker.tag as Place
        _homeViewModel.updateBusinessPlace(tagPlace.toBusiness())
        return false
    }

    override fun onStart() {
        super.onStart()
       /* if(isMapReady()){
            updateLocation()
            Log.d("HomeFragment","onStart!")
        }*/
    }




     /* **** Test **** */


    private fun dummyInit(){
        CoroutineScope(Dispatchers.Main).launch {
            val job1= CoroutineScope(Dispatchers.IO).async {
                //_homeViewModel.deleteAllDoc()
                createPlaceDummy()
                createDocumentDummy()
                createPatientDummy()
                createCareServiceDummy()
            }
            job1.await()
            updateLocation()
        }

    }

    fun createDocumentDummy(){
        var dNo = 1
        for(pNo in 1..10) {
            val list = mutableListOf<Document>()
            val time = android.icu.util.Calendar.getInstance()
            time.set(android.icu.util.Calendar.YEAR,2022)
            time.set(android.icu.util.Calendar.MONTH,0)
            time.set(android.icu.util.Calendar.DAY_OF_MONTH,1)
            repeat(365/pNo) {
                val date = AppTime.SDF.format(time.time)
                val doc = Document(dNo, pNo, 0, date, "$pNo's document$dNo")
                list.add(doc)
                time.add(android.icu.util.Calendar.DAY_OF_MONTH, 1)
                _homeViewModel.setDoc(doc)
                dNo++
            }
        }

    }
    fun createPatientDummy() {
        val list = mutableListOf<Patient>()
        repeat(10){
            val no =  if (it <= 5) 0 else 1
            val bp = DummyDataUtil.placeList[no].bpNo
            list.add(Patient(it,bp,"name1$it","19010101","M"))
        }
        list.forEach {
            _homeViewModel.setPatient(it)
        }
    }
    fun createPlaceDummy(){
        val list = mutableListOf<BusinessPlace>()
        DummyDataUtil.placeList.forEachIndexed(){no,item->
            list.add(item)
        }
        list.forEach {
            _homeViewModel.setBusinessPlace(it)
        }
    }
    fun createCareServiceDummy(){
        val list = mutableListOf<CareService>()
        DummyDataUtil.careServiceList.forEach(){service->
            _homeViewModel.setCareService(service)
        }

    }

    private fun docToIndex(docList: List<Document>): List<Int> {
        val list = mutableListOf<Int>()
        val cal = Calendar.getInstance()
        docList.forEach { doc->
            doc.crtDate.apply {
                cal.time = AppTime.SDF.parse(this)
                list.add(cal.get(Calendar.DAY_OF_MONTH))
            }
        }
        return list
    }
    private fun countToIndex(dataList: List<DataCount>):List<Int> {
        val list = mutableListOf<Int>()
        dataList.forEach { dc ->
            dc.data.toInt().apply { if(this!=0) list.add(this) } }
        return list
    }

}