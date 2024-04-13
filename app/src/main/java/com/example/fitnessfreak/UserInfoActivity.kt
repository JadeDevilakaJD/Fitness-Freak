package com.example.fitnessfreak
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UserInfoActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextHeight: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var btnSelectDateOfBirth: Button
    private lateinit var spinnerGender: Spinner
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var selectedDateOfBirth: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_layout)

        // Initialize Firebase Auth and Database
        auth = Firebase.auth
        database = Firebase.database.reference

        // Initialize views
        editTextName = findViewById(R.id.editTextName)
        editTextHeight = findViewById(R.id.editTextHeight)
        editTextWeight = findViewById(R.id.editTextWeight)
        btnSelectDateOfBirth = findViewById(R.id.btnSelectDateOfBirth)
        spinnerGender = findViewById(R.id.spinnerGender)

        // Populate the spinner with gender options
        val genderArray = resources.getStringArray(R.array.gender_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter

        btnSelectDateOfBirth.setOnClickListener {
            showDatePickerDialog()
        }
    }
    // Function to show DatePickerDialog
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDateOfBirth = dateFormat.format(selectedDate.time)
                btnSelectDateOfBirth.text = selectedDateOfBirth
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }


    // Called when the "Submit" button is clicked
    fun onSubmitClicked(view: View) {
        val name = editTextName.text.toString().trim()
        val height = editTextHeight.text.toString().trim()
        val weight = editTextWeight.text.toString().trim()
        val gender = spinnerGender.selectedItem.toString()

        // Validate user input
        if (name.isEmpty() || height.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Save user information to Firebase
        saveUserInfo(name, height, weight, gender)
    }

    private fun saveUserInfo(name: String, height: String, weight: String, gender: String) {
        // Get current user ID
        val userId = auth.currentUser?.uid

        // Save user information to Firebase Realtime Database
        userId?.let {
            val userInfo = mapOf(
                "name" to name,
                "height" to height,
                "weight" to weight,
                "gender" to gender
            )
            database.child("users").child(userId).setValue(userInfo)
                .addOnSuccessListener {
                    Toast.makeText(this, "User information saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save user information: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Called when the "Skip" button is clicked
    fun onSkipClicked(view: View) {
        // You can handle skipping user information here
        // For now, let's just finish the activity
        finish()
    }
}
