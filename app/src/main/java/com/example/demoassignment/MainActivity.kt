package com.example.demoassignment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoassignment.models.Customer
import com.example.demoassignment.models.CustomerResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

   private lateinit var recyclerView: RecyclerView
   private lateinit var adapter: CustomerAdapter
   private var customerList: MutableList<Customer> = mutableListOf()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)

      recyclerView = findViewById(R.id.recyclerView)
      recyclerView.layoutManager = LinearLayoutManager(this)

      fetchCustomers()

      val searchView: EditText = findViewById(R.id.searchView)
      searchView.addTextChangedListener(object : TextWatcher {
         override fun afterTextChanged(s: Editable?) {
            filter(s.toString())
         }

         override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

         override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
      })

      val addButton: Button = findViewById(R.id.addButton)
      addButton.setOnClickListener {
         showAddCustomerDialog()
      }
   }


   private fun showAddCustomerDialog() {
      val builder = AlertDialog.Builder(this)
      val inflater = layoutInflater
      val dialogView = inflater.inflate(R.layout.dialog_add_customer, null)
      builder.setView(dialogView)

      val nameInput: EditText = dialogView.findViewById(R.id.nameInput)
      val phoneInput: EditText = dialogView.findViewById(R.id.phoneInput)
      val submitButton: Button = dialogView.findViewById(R.id.submitButton)

      val dialog = builder.create()

      submitButton.setOnClickListener {
         val name = nameInput.text.toString()
         val phone = phoneInput.text.toString()

         if (name.isNotEmpty() && phone.isNotEmpty()) {
            val newCustomer = Customer(name = name, mobileNo = phone, isCow = 0 , isBuffalo = 1)
            customerList.add(newCustomer)
            adapter.notifyDataSetChanged()
            dialog.dismiss()
         } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
         }
      }

      dialog.show()
   }

   private fun fetchCustomers() {
      RetrofitClient.apiService.getCustomerDetails(1, 10, 787).enqueue(object : Callback<CustomerResponse> {
         override fun onResponse(call: Call<CustomerResponse>, response: Response<CustomerResponse>) {
            if (response.isSuccessful) {
               Log.e("response" , response.toString())
               customerList = response.body()?.data?.toMutableList() ?: mutableListOf()
               adapter = CustomerAdapter(customerList)
               recyclerView.adapter = adapter
               Log.e("customer" , customerList.toString())
            }
         }

         override fun onFailure(call: Call<CustomerResponse>, t: Throwable) {
            // Handle failure
            Log.d("response failed" , "failed")
         }
      })
   }

   private fun filter(text: String) {
      val filteredList = customerList.filter { it.name.contains(text, true) }
      adapter = CustomerAdapter(filteredList)
      recyclerView.adapter = adapter
   }
}
