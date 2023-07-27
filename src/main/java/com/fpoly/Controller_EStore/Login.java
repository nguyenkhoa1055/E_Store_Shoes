package com.fpoly.Controller_EStore;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.fpoly.Entity.User;
import com.fpoly.Service.UserService;
import com.fpoly.Utils.CookieService;
import com.fpoly.Utils.HashPass;
import com.fpoly.Utils.SessionService;

@Controller
public class Login {
	
	@Autowired
	UserService userService;

	@Autowired
	CookieService cookie;
	
	@Autowired 
	SessionService sessionService;
	
	@Autowired 
	HashPass hashPass;
	
	@Autowired
	HttpSession session;
	
	@GetMapping("login")
	public String login (@ModelAttribute("user") User  us,@ModelAttribute("userLogin") User  u) {
		return "User/SignIn_Up";
	}
	
	@PostMapping("login/sign-up")
	public String loginU (@ModelAttribute("user") User  us) {
		
		String pass = hashPass.hash(us.getPassword());
		us.setPassword(pass);		
	//	us.setPassword(hashPass.hash(String.valueOf(us.getPassword())));
		us.setActivated(0); // 0 = On, 1 = off
		us.setAdmin(2);  // 0 = Admin, 1 = NhienVien, 2 = Khach hang
		userService.addUser(us);	
		return "redirect:/login";
	}
	
	@PostMapping("login/home")
	public String loginHome (@Valid @ModelAttribute("userLogin") User  us, BindingResult result) {
	
		User u = userService.findUser(String.valueOf(us.getUsername()));
		
		if(u != null) {
			Boolean checkPass = hashPass.verify(us.getPassword(), u.getPassword());	
			if(u.getUsername().equals(us.getUsername()) && checkPass == true) {
				sessionService.set("user", u);
				cookie.add("uName", u.getUsername(), 10);
				cookie.add("uPass", u.getUsername(), 10);
				return "redirect:/home";
			}
		}
		session.setAttribute("message", "Error UserName or Password");
		return "redirect:/login";
	}
	
	@GetMapping("/logout")
	public String logout() {	
		cookie.remove("uName");
		cookie.remove("uPass");		
		sessionService.set("user", null);
		return "redirect:/login";
	}
}
