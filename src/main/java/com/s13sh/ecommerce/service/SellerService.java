package com.s13sh.ecommerce.service;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.s13sh.ecommerce.dto.Product;
import com.s13sh.ecommerce.dto.Seller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

public interface SellerService {

	String loadRegister(ModelMap map,Seller seller);

	String loadRegister(Seller seller, BindingResult result, HttpSession session);

	String submitOtp(int id, int otp, HttpSession session);

	String loadHome(HttpSession session);

	String addProduct(HttpSession session,Product product,ModelMap map);

	String addProduct(HttpSession session, @Valid Product product, BindingResult result, MultipartFile image);

	String viewProducts(HttpSession session, ModelMap map);

	String deleteProduct(HttpSession session, int id);

	String editProduct(HttpSession session, int id, ModelMap map);

	String updateProduct(HttpSession session, @Valid Product product, BindingResult result, MultipartFile image);

    String resendOtp(int id, HttpSession session);

}
