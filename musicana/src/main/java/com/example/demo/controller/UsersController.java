package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entities.Song;
import com.example.demo.entities.Users;
import com.example.demo.services.SongService;
import com.example.demo.services.UsersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsersController {
	@Autowired
	UsersService service;
	
	@Autowired
	SongService songService;
	
	@PostMapping("/register")
	public String addUsers(@ModelAttribute Users user){
		boolean user_status= service.emailExists(user.getEmail());
		if(user_status==false)
		{
			service.addUser(user);
			System.out.println("user added");
			
		}
		else {
			System.out.println("email already exists");
		}
		return "login";
	}
	
	@PostMapping("/validate")
	public String validateUser(@RequestParam String email,
								@RequestParam String password,
								HttpSession session,Model model) {
		if(service.validateUser(email,password)==true) {
			String role=service.getUserRole(email);
			session.setAttribute("email", email);
			if(role.equals("admin")) {
				return "adminHome";
			}
			else {
				Users user = service.getUser(email);
				boolean userStatus = user.isPremium();
				List<Song> songsList = songService.fetchAllSongs();
				model.addAttribute("songs", songsList);
				model.addAttribute("isPremium", userStatus);
				if(userStatus==false) {
					return "customerHome";
				}
				else {
					return "payment-success";
				}
				
				
			}
		}
		else {
			return "login";
		}
	}
	
	
	@GetMapping("/payment-success")
	public String customerHome(HttpSession session) {
		String email=(String)session.getAttribute("email");
		Users user=service.getUser(email);
		user.setPremium(true);
		service.updateUser(user);
		return "payment-success";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "login";
	}
	
	

}
