package com.example.loginsignup.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.loginsignup.R;
import com.example.loginsignup.databinding.FragmentLoginBinding;
import com.example.loginsignup.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class SignUpFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FragmentSignUpBinding binding;
    private FirebaseAuth mAuth;
    private String email, password, repassword, name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment fragment = new LoginFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment, "thisfragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.btnProceed.setOnClickListener(v -> {

            email = binding.etEmail.getText().toString();
            password = binding.etPswrd.getText().toString();
            repassword = binding.etRePswrd.getText().toString();
            name = binding.etName.getText().toString();

            String regex = "^(.+)@(.+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);

            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

                if (matcher.matches()) {
                    if (!(password.length() < 8)) {

                        if (password.equals(repassword)) {
                            createUser();
                        } else
                            Toast.makeText(getContext(), "Password didn't match", Toast.LENGTH_SHORT).show();

                    } else
                        Toast.makeText(getContext(), "Minimum password length is 8", Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(getContext(), "Invalid mail-id", Toast.LENGTH_SHORT).show();

            } else
                Toast.makeText(getContext(), "Please provide all valid entries", Toast.LENGTH_SHORT).show();

        });

    }

    private void createUser() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in successful
                        Map<String, Object> userName = new HashMap<>();
                        userName.put("name", name);

                        db.collection("users").document(String.valueOf(mAuth.getUid()))
                                .set(userName)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        startActivity(new Intent(getActivity(), HomeActivity.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Failure", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        //sign in fails
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}