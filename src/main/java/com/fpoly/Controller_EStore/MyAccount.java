package com.fpoly.Controller_EStore;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fpoly.Entity.Order;
import com.fpoly.Entity.OrdersDetails;
import com.fpoly.Entity.User;
import com.fpoly.Repository.OrderDAO;
import com.fpoly.Repository.Order_DetailDAO;
import com.fpoly.Service.Order_DetailService;
import com.fpoly.Service.UserService;
import com.fpoly.Utils.CookieService;

@Controller
public class MyAccount {
	
	@Autowired
	UserService userService;
	
	@Autowired
	CookieService cookie;
	
	@Autowired
	Order_DetailService detailService;
	
	@Autowired
	OrderDAO daoO;
	
	@Autowired
	Order_DetailDAO daoPD;
	
	@GetMapping("/account")
	public String account(Model model, @ModelAttribute("accountUser") User us) {
		
		// lấy tên username trong cookie
		String username = cookie.getValue("uName");	
		if (us.equals(null)) {	
			return "redirect:/login";
		}
		if(username !=null) {		
		// set username trong findUser để lấy ra user
		 us = userService.findUser(username); // để tính bảo mật nên trên profile k hiển thị mật khẩu
		
		//Find danh sách đang đặt hàng ( trong DB k có giá nên lấy table Order-detail )
		List<OrdersDetails> listOrder = detailService.findAllOrderById(us.getId());
		
		model.addAttribute("accountUser",us);
		model.addAttribute("listOrder",listOrder);
		}
		
		return "User/MyAccount";
	}
	
	@GetMapping("/cancel-order/{id}")
	public String Cancel(@PathVariable("id") Integer idO) {
		Order order = daoO.getReferenceById(idO);
		daoO.delete(order);
		return"redirect:/account";
	}
	
	@ResponseBody
	@GetMapping("/order-viewid/{id}")
	public List<Object[]> detail(Model model,@PathVariable("id") Integer idO) {
		List<Object[]> object = daoPD.findAllOrderDetailUser(idO);
		model.addAttribute("detail", daoPD.findAllOrderDetailUser(idO));
		return object;
	}
}
