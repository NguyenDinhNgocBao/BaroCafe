package com.example.coffeecart.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.barocafe.R
import com.example.barocafe.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun registerEvents() {
        binding.txtAuth.setOnClickListener {
            navControl.navigate(R.id.action_signInFragment_to_signUpFragment2)
        }
        binding.btnNext.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val pass = binding.edtPassword.text.toString().trim()

            if(email.isNotEmpty() && pass.isNotEmpty()){
                binding.progressBar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(context, "Login Successfully", Toast.LENGTH_LONG).show()
                        navControl.navigate(R.id.action_signInFragment_to_homeFragment)

                    }else{
                        Toast.makeText(context, "Wrong password or email", Toast.LENGTH_LONG).show()
                    }
                    binding.progressBar.visibility = View.GONE
                }
            }else{
                Toast.makeText(context,"Empty Information", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun init(view: View) {
        auth = FirebaseAuth.getInstance()
        navControl = Navigation.findNavController(view)
    }

}