package com.example.sulrammi.myapplication

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.EditText
import android.widget.Toast
import com.example.sulrammi.myapplication.MainActivity.Constants.FIREBASE_ITEM
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*


class ToDoItem {

    companion object Factory {
        fun create(): ToDoItem = ToDoItem()
    }

    var objectId: String = ""
    var itemText: String? = null
    var done: Boolean = false
}

class MainActivity : AppCompatActivity(), ItemRowListener {

    override fun modifyItemState(itemObjectId: String, isDone: Boolean) {
        val itemReference = mDatabase.child(Constants.FIREBASE_ITEM).child(itemObjectId)
        itemReference.child("done").setValue(isDone);
    }

    override fun onItemDelete(itemObjectId: String) {
        val itemReference = mDatabase.child(Constants.FIREBASE_ITEM).child(itemObjectId)
        //deletion can be done via removeValue() method
        itemReference.removeValue()
    }

    lateinit var adapter: ToDoItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        items_list.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener { view ->
            addNewItemDialog()
        }

        mDatabase = FirebaseDatabase.getInstance().reference

        val query = mDatabase.child(FIREBASE_ITEM).orderByKey()

        val options = FirebaseRecyclerOptions.Builder<ToDoItem>()
            .setQuery(query, ToDoItem::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = ToDoItemAdapter(options, this)
        items_list.adapter = adapter
    }

    lateinit var mDatabase: DatabaseReference

    object Constants {
        @JvmStatic
        val FIREBASE_ITEM: String = "todo_item"
    }

    private fun addNewItemDialog() {


        val alert = AlertDialog.Builder(this)

        val itemEditText = EditText(this)
        alert.setMessage("날짜와 함께 준비물 추가 예)11월 22일 준비물 : 풀")
        alert.setTitle("오늘의 준비물 작성하기")

        alert.setView(itemEditText)

        alert.setPositiveButton("보내기") { dialog, positiveButton ->

            val todoItem = ToDoItem.create()
            todoItem.itemText = itemEditText.text.toString()
            todoItem.done = false

            //we first make a push so that a new item is made with a unique id

            val newItem = mDatabase.child(Constants.FIREBASE_ITEM).push()
            todoItem.objectId = newItem.key!!

            //then, we used the reference to set the value on that ID
            newItem.setValue(todoItem)

            dialog.dismiss()
            Toast.makeText(this, "Item saved with ID" + todoItem.objectId, Toast.LENGTH_SHORT).show()

        }

        alert.show()
    }

}














