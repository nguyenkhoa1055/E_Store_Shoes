package com.fpoly.Controller_EStore;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpoly.Entity.Order;
import com.fpoly.Entity.OrdersDetails;
import com.fpoly.Entity.Product;
import com.fpoly.Entity.User;
import com.fpoly.Repository.OrderDAO;
import com.fpoly.Repository.Order_DetailDAO;
import com.fpoly.Repository.ProductDAO;
import com.fpoly.Repository.UserDAO;
import com.fpoly.Utils.CookieService;

@Controller
public class CheckOut {
//	
//	@Autowired
//	Product_Detail idP;
//	
	@Autowired
	ProductDAO Pdao;
	
	@Autowired
	UserDAO Udao;
	
	@Autowired
	CookieService cookieService;
	
	@Autowired
	OrderDAO odao;
	
	@Autowired
	Order_DetailDAO oddao;
	
	private int idP;
	private int quantity;
	@PostMapping("/checkout")
	public String checkout (
			@RequestParam("id") int idProduct,
			@RequestParam("quantity") int quantity,
			@RequestParam("color") String color,
			@RequestParam("size") String size,
			Model model
			) {
		Product pr = Pdao.getReferenceById(idProduct);
		model.addAttribute("product", pr);	
		model.addAttribute("quantity", quantity);	
		model.addAttribute("color", color);	
		model.addAttribute("size", size);	
		idP = pr.getId();
		this.quantity = quantity;
		return "User/checkout";
	}
	
	@PostMapping("/pay")
	public String pay(@RequestParam("address") String address) {
		
		String userName = cookieService.getValue("uName");
		if(!userName.equals("")) {
			User us = Udao.findByUserName(userName);
			
			Order order = new Order();
			order.setUser(us);
			order.setAddress(address);
			order.setCreatedate(new Date());
			odao.save(order);
			Product pr = Pdao.getReferenceById(idP);
			System.out.println(pr.getId());
			OrdersDetails detail = new OrdersDetails();
			detail.setPrice(pr.getPrice());
			detail.setProduct(pr);
			detail.setOrder(order);
			detail.setQuantity(quantity);
			detail.setStatus(2);
			
			oddao.save(detail);
		}else {
			return "redirect:/login";
		}
		
	
		return "redirect:/product-detail"+ "?id="+idP;
	}
	
}
