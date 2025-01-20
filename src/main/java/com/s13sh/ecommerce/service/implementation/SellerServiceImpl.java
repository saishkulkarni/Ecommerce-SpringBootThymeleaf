package com.s13sh.ecommerce.service.implementation;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.s13sh.ecommerce.dto.Product;
import com.s13sh.ecommerce.dto.Seller;
import com.s13sh.ecommerce.helper.AES;
import com.s13sh.ecommerce.helper.CloudinaryHelper;
import com.s13sh.ecommerce.helper.MyEmailSender;
import com.s13sh.ecommerce.repository.CustomerRepository;
import com.s13sh.ecommerce.repository.ProductRepository;
import com.s13sh.ecommerce.repository.SellerRepository;
import com.s13sh.ecommerce.service.SellerService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class SellerServiceImpl implements SellerService {

	@Autowired
	CloudinaryHelper cloudinaryHelper;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	MyEmailSender emailSender;

	@Autowired
	SellerRepository sellerRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Override
	public String loadRegister(ModelMap map, Seller seller) {
		map.put("seller", seller);
		return "seller-register.html";
	}

	@Override
	public String loadRegister(Seller seller, BindingResult result, HttpSession session) {
		if (!seller.getPassword().equals(seller.getConfirmpassword()))
			result.rejectValue("confirmpassword", "error.confirmpassword", "* Password Missmatch");
		if (customerRepository.existsByEmail(seller.getEmail()) || sellerRepository.existsByEmail(seller.getEmail()))
			result.rejectValue("email", "error.email", "* Email should be Unique");
		if (customerRepository.existsByMobile(seller.getMobile())
				|| sellerRepository.existsByMobile(seller.getMobile()))
			result.rejectValue("mobile", "error.mobile", "* Mobile Number should be Unique");

		if (result.hasErrors())
			return "seller-register.html";
		else {
			int otp = new Random().nextInt(100000, 1000000);
			seller.setOtp(otp);
			seller.setPassword(AES.encrypt(seller.getPassword(), "123"));
			sellerRepository.save(seller);
			emailSender.sendOtp(seller);

			session.setAttribute("success", "Otp Sent Success");
			session.setAttribute("id", seller.getId());
			return "redirect:/seller/otp";
		}
	}

	@Override
	public String submitOtp(int id, int otp, HttpSession session) {
		Seller seller = sellerRepository.findById(id).orElseThrow();
		if (seller.getOtp() == otp) {
			seller.setVerified(true);
			sellerRepository.save(seller);
			session.setAttribute("success", "Account Created Success");
			return "redirect:/";
		} else {
			session.setAttribute("failure", "Invalid OTP");
			session.setAttribute("id", seller.getId());
			return "redirect:/seller/otp";
		}
	}

	@Override
	public String loadHome(HttpSession session) {
		if (session.getAttribute("seller") != null)
			return "seller-home.html";
		else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String addProduct(HttpSession session, Product product, ModelMap map) {
		if (session.getAttribute("seller") != null) {
			map.put("product", product);
			return "add-product.html";
		} else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String addProduct(HttpSession session, @Valid Product product, BindingResult result, MultipartFile image) {
		if (session.getAttribute("seller") != null) {
			if (result.hasErrors()) {
				return "add-product.html";
			} else {
				product.setSeller((Seller) session.getAttribute("seller"));
				product.setImageLink(cloudinaryHelper.saveImage(image));
				productRepository.save(product);

				session.setAttribute("success", "Product Added Success");
				return "redirect:/seller/home";
			}
		} else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String viewProducts(HttpSession session, ModelMap map) {
		if (session.getAttribute("seller") != null) {
			Seller seller = (Seller) session.getAttribute("seller");
			List<Product> products = productRepository.findBySeller_id(seller.getId());
			if (products.isEmpty()) {
				session.setAttribute("failure", "No Products Added Yet");
				return "redirect:/seller/home";
			} else {
				map.put("products", products);
				return "seller-products.html";
			}
		} else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String deleteProduct(HttpSession session, int id) {
		if (session.getAttribute("seller") != null) {
			productRepository.deleteById(id);
			session.setAttribute("success", "Product Deleted Success");
			return "redirect:/seller/manage-products";
		} else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String editProduct(HttpSession session, int id, ModelMap map) {
		if (session.getAttribute("seller") != null) {
			Product product = productRepository.findById(id).orElseThrow();
			map.put("product", product);
			return "edit-product.html";
		} else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String updateProduct(HttpSession session, @Valid Product product, BindingResult result,
			MultipartFile image) {
		if (session.getAttribute("seller") != null) {
			if (result.hasErrors()) {
				return "edit-product.html";
			} else {
				product.setSeller((Seller) session.getAttribute("seller"));

				byte[] picture;
				try {
					picture = new byte[image.getInputStream().available()];
					image.getInputStream().read(picture);

					if (picture.length > 0)
						product.setImageLink(cloudinaryHelper.saveImage(image));
					else
						product.setImageLink(productRepository.findById(product.getId()).orElseThrow().getImageLink());

				} catch (IOException e) {

				}

				productRepository.save(product);

				session.setAttribute("success", "Product Updated Success");

				return "redirect:/seller/manage-products";
			}
		} else {
			session.setAttribute("failure", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@Override
	public String resendOtp(int id, HttpSession session) {
		Seller seller = sellerRepository.findById(id).orElseThrow();
		int otp = new Random().nextInt(100000, 1000000);
		seller.setOtp(otp);
		sellerRepository.save(seller);
		emailSender.sendOtp(seller);

		session.setAttribute("success", "Otp Resent Success");
		session.setAttribute("id", seller.getId());
		return "redirect:/seller/otp";
	}

}
