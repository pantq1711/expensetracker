package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.UserDTO;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok tu tao constructor inject UserRepository
public class UserService {
    //Khong dung @Autowired - inject qua constructor la best practice
    private final UserRepository userRepository;

    // Lay tat ca users, convert sang DTO
    public List<UserDTO> getAllUser(){
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO) // voi moi User, goi convertToDTO
                .toList();
    }

    // Lay 1 user theo id
    public UserDTO getUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found!" + id));
                return convertToDTO(user);
    }

    // Xoa 1 user theo id

    public void deleteUserById(Long id){
        if(!userRepository.existsById(id)){
            throw new RuntimeException("User not found!" + id);
        }
        userRepository.deleteById(id);
    }

    // Convert Entity -> DTO, khong tra password ra ngoai
    private UserDTO convertToDTO(User user){
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
