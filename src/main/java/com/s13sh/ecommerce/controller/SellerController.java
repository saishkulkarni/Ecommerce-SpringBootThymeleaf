package com.s13sh.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.s13sh.ecommerce.dto.Product;
import com.s13sh.ecommerce.dto.Seller;
import com.s13sh.ecommerce.service.SellerService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/seller")
public class SellerController {
	
	@Autowired
	SellerService sellerService;

	@GetMapping("/register")
	public String loadRegister(ModelMap map,Seller seller) {
		return sellerService.loadRegister(map,seller);
	}
	
	@PostMapping("/register")
	public String register(@Valid Seller seller,BindingResult result,HttpSession session) {
		return sellerService.loadRegister(seller,result,session);
	}
	
	@GetMapping("/otp")
	public String loadOtpPage() {
		return "seller-otp.html";
	}
	
	@PostMapping("/submit-otp/{id}")
	public String submitOtp(@PathVariable int id,@RequestParam int otp,HttpSession session) {
		return sellerService.submitOtp(id,otp,session);
	}
	
	@GetMapping("/home")
	public String loadHome(HttpSession session) {
		return sellerService.loadHome(session);
	}
	
	@GetMapping("/add-product")
	public String addProduct(HttpSession session,Product product,ModelMap map) {
		return sellerService.addProduct(session,product,map);
	}
	
	@PostMapping("/add-product")
	public String addProduct(HttpSession session,@Valid Product product,BindingResult result,@RequestParam MultipartFile image) {
		return sellerService.addProduct(session,product,result,image);
	}
	
	@GetMapping("/manage-products")
	public String viewProducts(HttpSession session,ModelMap map) {
		return sellerService.viewProducts(session,map);
	}
	
	@GetMapping("/delete-product/{id}")
	public String deleteProduct(HttpSession session,@PathVariable int id) {
		return sellerService.deleteProduct(session,id);
	}
	
	@GetMapping("/edit-product/{id}")
	public String editProduct(HttpSession session,@PathVariable int id,ModelMap map) {
		return sellerService.editProduct(session,id,map);
	}
	
	@PostMapping("/edit-product")
	public String updateProduct(HttpSession session,@Valid Product product,BindingResult result,@RequestParam MultipartFile image) {
		return sellerService.updateProduct(session,product,result,image);
	}

	@GetMapping("/resend-otp/{id}")
	public String resendOtp(@PathVariable int id,HttpSession session) {
		return sellerService.resendOtp(id,session);
	}
	
}
