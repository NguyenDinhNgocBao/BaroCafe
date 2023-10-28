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
import com.example.barocafe.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navControl : NavController
    private lateinit var binding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun registerEvents() {
        binding.txtAuth.setOnClickListener {
            navControl.navigate(R.id.action_signUpFragment_to_signInFragment2)
        }
        binding.btnNext.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val pass = binding.edtPassword.text.toString().trim()
            val rePass = binding.edtRePassword.text.toString().trim()

            if(email.isNotEmpty() && pass.isNotEmpty() && rePass.isNotEmpty()){
                binding.progressBar.visibility = View.VISIBLE
                if(rePass.equals(pass)){
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(context,"Successfully", Toast.LENGTH_SHORT).show()
                            navControl.navigate(R.id.action_signUpFragment_to_signInFragment2)
                        }else{
                            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }else{
                    Toast.makeText(context,"Re-entered password is incorrect",Toast.LENGTH_SHORT).show()
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