package com.example.travelday_2

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.init
import com.example.travelday_2.databinding.DateListRowBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.util.*
import kotlin.collections.ArrayList

class DateListAdapter(val context: Context, val items: ArrayList<SharedViewModel.Date>) :
    RecyclerView.Adapter<DateListAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(data: SharedViewModel.Date)

        fun onOutfitClick(data:SharedViewModel.Date)
    }
    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(val binding: DateListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SharedViewModel.Date) {
            binding.innerRecyclerview.layoutManager = LinearLayoutManager(context)

            val adapter =DailyScheduleAdapter(item.dailyScheduleList)
            adapter.itemClickListener = object:DailyScheduleAdapter.OnItemClickListener {
                override fun OnItemClick(data: SharedViewModel.DailySchedule) {
                    adapter.removeItem(adapterPosition)
                }
            }

             binding.innerRecyclerview.adapter = adapter


        }
        init {
            binding.addButtonNew.setOnClickListener {
                itemClickListener?.onItemClick(items[adapterPosition])
            }

            binding.outfitBtn.setOnClickListener {
                val fragment = OutfitFragment()
                val fragmentManager = (binding.root.context as Fragment).parentFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frag_container, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }


    }
    fun moveItem(oldPos:Int, newPos:Int){
        val item = items[oldPos]
        items.removeAt(oldPos)
        items.add(newPos,item)
        notifyItemMoved(oldPos, newPos)
    }
    fun removeItem(pos: Int){
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DateListRowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }
    override fun getItemCount(): Int {
        return items.size    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        // 문자열 날짜를 LocalDate로 변환
        val formatter = DateTimeFormatterBuilder()
            .appendPattern("MM.dd")
            .parseDefaulting(ChronoField.YEAR, LocalDate.now().year.toLong())
            .toFormatter()
        val date = LocalDate.parse(item.date, formatter)

        // 요일 이름 얻기
        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

        // 요일 이름의 첫 글자만 추출
        val firstLetterOfDay = dayOfWeek[0]

        // 날짜와 요일을 출력
        holder.binding.dateTextView.text = "•${item.date} ($firstLetterOfDay)"

        holder.binding.outfitBtn.setOnClickListener{
            itemClickListener?.onOutfitClick(item)
        }
    }
}

