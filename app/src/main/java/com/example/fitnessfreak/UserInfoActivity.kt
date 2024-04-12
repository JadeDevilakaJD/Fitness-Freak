package com.example.fitnessfreak

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UserInfoActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextAge: EditText
    private lateinit var spinnerGender: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_layout)

        // Initialize views
        editTextName = findViewById(R.id.editTextName)
        editTextAge = findViewById(R.id.editTextAge)
        spinnerGender = findViewById(R.id.spinnerGender)

        // Populate the spinner with gender options
        ArrayAdapter.createFromResource(
            this,
            R.array.genders_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGender.adapter = adapter
        }
    }

    // Called when the "Submit" button is clicked
    fun onSubmitClicked(view: View) {
        val name = editTextName.text.toString().trim()
        val age = editTextAge.text.toString().trim()
        val gender = spinnerGender.selectedItem.toString()

        // Validate user input
        if (name.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, "Please enter your name and age", Toast.LENGTH_SHORT).show()
            return
        }

        // Save the user information (you can implement your own logic here)
        // For now, let's just display a toast message
        val userInfo = "Name: $name\nAge: $age\nGender: $gender"
        Toast.makeText(this, userInfo, Toast.LENGTH_SHORT).show()

        // You can also save this information to Firebase or any other backend
    }

    // Called when the "Skip" button is clicked
    fun onSkipClicked(view: View) {
        // You can handle skipping user information here
        // For now, let's just finish the activity
        finish()
    }
}
