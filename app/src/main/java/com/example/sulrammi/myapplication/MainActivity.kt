package com.example.sulrammi.myapplication
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ItemRowListener {

    override fun modifyItemState(itemObjectId: String, isDone: Boolean) {
        val itemReference = mDatabase.child(Constants.FIREBASE_ITEM).child(itemObjectId)
        itemReference.child("done").setValue(isDone);
    }

    override fun onItemDelete(itemObjectId: String) {
        val itemReference = mDatabase.child(Constants.FIREBASE_ITEM).child(itemObjectId)
        //deletion can be done via removeValue() method
        itemReference.removeValue()

        adapter.notifyDataSetChanged()
    }

    var toDoItemList: MutableList<ToDoItem>? = null
        lateinit var adapter: ToDoItemAdapter

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)


            fab.setOnClickListener { view ->

                addNewItemDialog()
            }
            mDatabase = FirebaseDatabase.getInstance().reference
            toDoItemList = mutableListOf<ToDoItem>()
            adapter = ToDoItemAdapter(this, toDoItemList!!)
            items_list.setAdapter(adapter)
            mDatabase.orderByKey().addValueEventListener(itemListener)
        }

        lateinit var mDatabase: DatabaseReference

        class ToDoItem {

            companion object Factory {
                fun create(): ToDoItem = ToDoItem()
            }

            var objectId: String? = null
            var itemText: String? = null
            var done: Boolean? = false
        }

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
                todoItem.objectId = newItem.key

                //then, we used the reference to set the value on that ID
                newItem.setValue(todoItem)

                dialog.dismiss()
                Toast.makeText(this, "Item saved with ID" + todoItem.objectId, Toast.LENGTH_SHORT).show()

            }

            alert.show()
        }

        var itemListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                toDoItemList!!.clear()
                addDataToList(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Item failed, log a message
                Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
            }
        }

        private fun addDataToList(dataSnapshot: DataSnapshot) {
            val items = dataSnapshot.children.iterator()
            //Check if current database contains any collection
            if (items.hasNext()) {
                val toDoListindex = items.next()
                val itemsIterator = toDoListindex.children.iterator()

                //check if the collection has any to do items or not
                while (itemsIterator.hasNext()) {
                    //get current item
                    val currentItem = itemsIterator.next()
                    val todoItem = ToDoItem.create()
                    //get current data in a map
                    val map = currentItem.getValue() as HashMap<String, Any>
                    //key will return Firebase ID
                    todoItem.objectId = currentItem.key
                    todoItem.done = map.get("done") as Boolean?
                    todoItem.itemText = map.get("itemText") as String?
                    toDoItemList!!.add(todoItem);
                }
            }
            //alert adapter that has changed
            adapter.notifyDataSetChanged()
        }


    }














