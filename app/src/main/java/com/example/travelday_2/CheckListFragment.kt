package com.example.newteamproj

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.ParseException
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelday_2.databinding.FragmentCheckListBinding


class CheckListFragment : Fragment() {
    lateinit var binding:FragmentCheckListBinding
    var array: ArrayList<CheckListData> = ArrayList<CheckListData>()
    lateinit var adapter:CheckListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCheckListBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
    }
    private fun setupData() {
        binding.recycler.layoutManager= LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        adapter=CheckListAdapter(array)

        adapter.onCheck=object:CheckListAdapter.CheckBoxChangeListener{ //체크박스 선택
            override fun onCheckBoxChanged(
                data: CheckListData, pos: Int, holder: CheckListAdapter.viewHolder
            ) {
                data.check = !data.check
                adapter.notifyItemChanged(pos)
            }
        }
        binding.recycler.adapter=adapter

        adapter.onChange=object:CheckListAdapter.AdapterListener{ //체크리스트 수정하기
            override fun onValueReturned(data: CheckListData,pos:Int,value: String) {
                binding.textcheck.setText(value)
                adapter.removeItem(pos)
            }
        }

        binding.addButton.setOnClickListener {//체크리스트 추가하기
            //if(binding.textcheck.length()>=1){
                array.add(CheckListData(binding.textcheck.text.toString(),false))
                adapter.notifyDataSetChanged()
                binding.textcheck.setText("")
            //}
            //else Toast.makeText(context, "한글자 이상 입력해주세요", Toast.LENGTH_SHORT).show()
        }
        binding.alldelete.setOnClickListener {//체크리스트 모두 지우기
            //val count:Int=adapter.itemCount
            adapter.deleteItem()
        }


        binding.addlinkbtn.setOnClickListener { //링크 추가하기

            binding.linktext.append("\n"+binding.linkaddtext.text.toString())
            binding.linkaddtext.setText("")
            val pattern = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$"
            val linkcheck = binding.linktext.text.toString().matches(pattern.toRegex())

            if (linkcheck){
                binding.linktext.paintFlags= Paint.UNDERLINE_TEXT_FLAG
                binding.linktext.setTextColor(Color.BLUE)
                binding.linktext.setTypeface(null, Typeface.BOLD)
            }

            //Toast.makeText(context, "텍스트가 링크로 저장되었습니다.", Toast.LENGTH_SHORT).show()

        }

        binding.linktext.setOnClickListener {//링크를 눌렀을때
            val link:String=binding.linktext.getText().toString()
            if (!link.isEmpty()) {
                try {
                    val uri: Uri = Uri.parse(link)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
                catch (e: ParseException){
                    //Toast.makeText(context, "유효하지 않은 링크", Toast.LENGTH_SHORT).show()
                }catch (e:java.lang.Exception){
                    //Toast.makeText(context, "유효하지 않은 링크", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.linktextdelete.setOnClickListener {//link 삭제
            binding.linktext.setText("")
        }
        binding.changelinktext.setOnClickListener {//link 수정
            binding.linkaddtext.setText(binding.linktext.text.toString())
            binding.linktext.setText("")
        }
    }
}