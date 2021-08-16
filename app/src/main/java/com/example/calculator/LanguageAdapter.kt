package com.example.calculator

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.Toast

class LanguageAdapter(var context: Context, var arrayList: ArrayList<LanguageItem>, var mainActivity: MainActivity):BaseAdapter() {

    override fun getItem(position: Int): Any {
        return arrayList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view:View = View.inflate(context, R.layout.button_item, null)

        var Button: Button = view.findViewById(R.id.buttonItem)

        var languageItem: LanguageItem = arrayList.get(position)
        var Name = languageItem.Name.toString()
        var Type = languageItem.Type.toString()

        Button.text = Name

        Button.setOnClickListener { if(Type == "Operation") mainActivity.onClickOperation(Name) else mainActivity.onClickNumber(Name) }

        return view
    }

}