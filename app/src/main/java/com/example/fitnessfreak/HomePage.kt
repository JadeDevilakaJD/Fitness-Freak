package com.example.fitnessfreak

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HomePage : AppCompatActivity(), SetGoalsDialogFragment.SetGoalsDialogListener {

    private lateinit var etWorkoutName: EditText
    private lateinit var etDuration: EditText
    private lateinit var etCaloriesBurned: EditText
    private lateinit var btnSaveWorkout: Button
    private lateinit var btnSetGoals: Button
    private lateinit var btnLogout: Button
    private lateinit var textViewFitnessGoals: TextView
    private lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        // Initialize views
        etWorkoutName = findViewById(R.id.etWorkoutName)
        etDuration = findViewById(R.id.etDuration)
        etCaloriesBurned = findViewById(R.id.etCaloriesBurned)
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout)
        btnSetGoals = findViewById(R.id.btnSetGoals)
        btnLogout = findViewById(R.id.btnLogout)
        textViewFitnessGoals = findViewById(R.id.textViewFitnessGoals)
        drawerLayout = findViewById(R.id.drawer_layout)


        // Set click listener for Save Workout button
        btnSaveWorkout.setOnClickListener {
            saveWorkoutData()
        }

        // Set click listener for Set Goals button
        btnSetGoals.setOnClickListener {
            openSetGoalsDialog()
        }

        // Set click listener for Logout button
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            // Redirect to MainActivity after logout
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Close the current activity
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_drawer -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    override fun onGoalsSet(goals: String) {
        textViewFitnessGoals.text = goals
    }

    private fun openSetGoalsDialog() {
        val dialogFragment = SetGoalsDialogFragment()
        dialogFragment.show(supportFragmentManager, "SetGoalsDialog")
    }

    private fun saveWorkoutData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            val database = FirebaseDatabase.getInstance().reference
            val workoutName = etWorkoutName.text.toString().trim()
            val duration = etDuration.text.toString().toIntOrNull() ?: 0
            val caloriesBurned = etCaloriesBurned.text.toString().toIntOrNull() ?: 0

            if (workoutName.isEmpty() || duration <= 0 || caloriesBurned <= 0) {
                Toast.makeText(this, "Please enter valid workout details", Toast.LENGTH_SHORT).show()
                return
            }

            val workoutId = database.child("workouts").child(uid).push().key ?: ""

            val workoutData = HashMap<String, Any>()
            workoutData["name"] = workoutName
            workoutData["duration"] = duration
            workoutData["caloriesBurned"] = caloriesBurned

            database.child("workouts").child(uid).child(workoutId).setValue(workoutData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Workout data saved successfully", Toast.LENGTH_SHORT).show()
                    etWorkoutName.text.clear()
                    etDuration.text.clear()
                    etCaloriesBurned.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save workout data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

class SetGoalsDialogFragment : DialogFragment() {

    interface SetGoalsDialogListener {
        fun onGoalsSet(goals: String)
    }

    private var listener: SetGoalsDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Set Fitness Goals")

        val input = EditText(requireActivity())
        builder.setView(input)

        builder.setPositiveButton("Save") { dialog, _ ->
            val goals = input.text.toString()
            listener?.onGoalsSet(goals)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SetGoalsDialogListener) {
            listener = context
        } else {
            // Log a message or handle it gracefully
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
