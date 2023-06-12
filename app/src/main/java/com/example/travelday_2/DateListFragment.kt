package com.example.travelday_2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.travelday_2.databinding.FragmentDateListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import java.net.URL


class DateListFragment : Fragment() {
    lateinit var binding:FragmentDateListBinding
    lateinit var adapter: DateListAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()
    lateinit var result :String
    val scope = CoroutineScope(Dispatchers.IO)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentDateListBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getWeather()
        init()
        initRecyclerView()
        initBackStack()
        //관찰
        sharedViewModel.countryList.observe(viewLifecycleOwner) { countryList ->
            adapter.notifyDataSetChanged()
        }
    }
    //환율 버튼과 날씨 버튼 눌렀을 때 구현
    private fun init(){
        result  = "weather"
        binding.weatherLayout.setOnClickListener {


                showWeatherDialog()
        }
        binding.exchangeLayout.setOnClickListener {
            val country = arguments?.getSerializable("클릭된 국가") as SharedViewModel.Country
            val bundle = Bundle().apply {
                putSerializable("클릭된 국가", country)

            }

            val exchangeFragment=ExchangeRateFragment().apply {
                arguments=bundle
            }
            parentFragmentManager.beginTransaction().apply {
                add(R.id.frag_container, exchangeFragment)
                hide(this@DateListFragment)
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun getWeather(){
        val country = arguments?.getSerializable("클릭된 국가") as SharedViewModel.Country
        val requestQueue = Volley.newRequestQueue(requireContext())
        val url = "http://api.openweathermap.org/data/2.5/weather?q="+country.name+"&appid="+"d74c3bbee7a3c497383271ff0d494542"

        val stringRequest = StringRequest(
            Request.Method.GET,url,
            {
                    response ->
                val jsonObject = JSONObject(response)

                val weatherJson = jsonObject.getJSONArray("weather")
                val weatherObj = weatherJson.getJSONObject(0)

                var weather = weatherObj.getString("description")
                //val imgURL = "http://openweathermap.org/img/w/" + weatherObj.getString("icon") + ".png"
                //Glide.with(this).load(imgURL).into(findViewById<ImageView>(R.id.weatherIcon))
                val tempK = JSONObject(jsonObject.getString("main"))
                val tempDo = (Math.round((tempK.getDouble("temp")-273.15)*100)/100.0)
                weather = weather + tempDo + "°C"
                //binding.result.text = weather
                result = weather

            },
            {
                Log.i("weahter",it.message.toString())
                result = "error"
            })

        requestQueue.add(stringRequest)
    }

    //weather dialog
    private fun showWeatherDialog() {
        getWeather()
        //val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_date_list, null)

        val country = arguments?.getSerializable("클릭된 국가") as SharedViewModel.Country

        val dialogBuilder = AlertDialog.Builder(requireContext())

            .setTitle(country.name)
            .setMessage(result)
            .setPositiveButton("확인", null)
            .setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }

        val dialog = dialogBuilder.create()
        dialog.show()



    }

    // 뒤로가기 버튼이 눌렸을 때 처리할 동작 구현
    private fun initBackStack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    @SuppressLint("SuspiciousIndentation", "SetTextI18n")
    private fun initRecyclerView() {
        val country = arguments?.getSerializable("클릭된 국가") as SharedViewModel.Country
        if (country != null) {
                adapter = DateListAdapter(requireContext(), country.dateList)
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager = LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL,false)}
                adapter.itemClickListener=object:DateListAdapter.OnItemClickListener{
                    override fun onItemClick(data: SharedViewModel.Date) {
                        val bundle = Bundle().apply {
                            putSerializable("클릭된 국가", country)
                            putSerializable("클릭된 날짜", data)
                        }
                        val dailyScheduleAddFragment=DailyScheduleAddFragment().apply {
                            arguments=bundle
                        }
                        parentFragmentManager.beginTransaction().apply {
                            add(R.id.frag_container, dailyScheduleAddFragment)
                            hide(this@DateListFragment)
                            addToBackStack(null)
                            commit()
                        }
                    }
                }
        //상단 탭에 국가이름, 날짜 데이터 및 디데이 표시
        val startDate = country.dateList.firstOrNull()?.date
        val endDate = country.dateList.lastOrNull()?.date
        val travelPeriod = if (startDate != null && endDate != null) {
            "$startDate ~ $endDate"
        } else {
            ""
        }
        binding.travelData.text=country.name +"\n " +travelPeriod
        binding.dDayDateList.text="D-"+country.dDay

       // swipe 시 remove기능 구현
        val simpleCallback=object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeItem(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper= ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }
}



