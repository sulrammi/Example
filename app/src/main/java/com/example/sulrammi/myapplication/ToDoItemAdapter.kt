package com.example.sulrammi.myapplication

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


class ToDoItemAdapter(options: FirebaseRecyclerOptions<ToDoItem>, listener: ItemRowListener) :
    FirebaseRecyclerAdapter<ToDoItem, ToDoItemAdapter.ListRowHolder>(options) {
    private var rowListener: ItemRowListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListRowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_items, parent, false)
        return ListRowHolder(view)
    }

    override fun onBindViewHolder(holder: ListRowHolder, position: Int, model: ToDoItem) {
        holder.label.text = model.itemText
        holder.isDone.isChecked = model.done
        holder.isDone.setOnClickListener {
            rowListener.modifyItemState(model.objectId, !model.done)
        }
        holder.ibDeleteObject.setOnClickListener {
            rowListener.onItemDelete(model.objectId)
        }
    }

    class ListRowHolder(row: View) : RecyclerView.ViewHolder(row) {
        val label: TextView = row.findViewById<TextView>(R.id.tv_item_text) as TextView
        val isDone: CheckBox = row.findViewById<CheckBox>(R.id.cb_item_is_done) as CheckBox
        val ibDeleteObject: ImageButton = row.findViewById<ImageButton>(R.id.iv_cross) as ImageButton
    }
}

