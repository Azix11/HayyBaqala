package com.hayy.baqala.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.hayy.baqala.databinding.FragmentProfileBinding;
import com.hayy.baqala.ui.auth.LoginActivity;
import com.hayy.baqala.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = SessionManager.getInstance(requireContext());

        // عرض بيانات المستخدم
        binding.tvName.setText(session.getUserName());
        binding.tvPhone.setText(session.getUserPhone());

        if (session.getUserEmail() != null && !session.getUserEmail().isEmpty()) {
            binding.tvEmail.setText(session.getUserEmail());
            binding.tvEmail.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmail.setVisibility(View.GONE);
        }

        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("تسجيل الخروج")
                .setMessage("هل تريد تسجيل الخروج؟")
                .setPositiveButton("نعم", (dialog, which) -> logout())
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void logout() {
        session.logout();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}