package com.example.demo.service;

import com.example.demo.dao.UserDaoImpl;
import com.example.demo.dto.User;
import com.example.demo.dto.UserFormDto;
import com.example.demo.dto.UserUpdatedFormDto;
import com.example.demo.exception.InternalServerError;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserFormInvalidException;
import com.example.demo.exception.UserNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl {

    private final UserDaoImpl userDao;

    @Autowired
    public UserServiceImpl(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

    public int count() {
        return userDao.count();
    }

    public int create(UserFormDto dto) {
        int rowCnt = 0;
        try {
            rowCnt = userDao.insert(dto);
            if (rowCnt != 1) {
                throw new InternalServerError("DB에 정상적으로 반영도지 못했습니다. 현재 적용된 로우수는 " + rowCnt + "입니다.");
            }
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExistsException("이미 존재하는 아이디입니다. " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new UserFormInvalidException("입력하신 데이터가 올바르지 않습니다. " + e.getMessage());
        }

        return rowCnt;
    }

    public User findById(String id) {
        var foundUser = userDao.selectById(id);
        if (foundUser == null) {
            throw new UserNotFoundException("해당 " + id + "를 가진 사용자를 찾을 수 없습니다.");
        }
        return foundUser;
    }

    public List<User> findAll() {
        return userDao.selectAll();
    }

    public int modify(UserUpdatedFormDto dto) {
        int rowCnt = 0;

        try {
            rowCnt = userDao.update(dto);
            if (rowCnt != 1) {
                throw new InternalServerError("DB에 정상적으로 반영도지 못했습니다. 현재 적용된 로우수는 " + rowCnt + "입니다.");
            }
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExistsException("이미 존재하는 아이디입니다. " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new UserFormInvalidException("입력하신 데이터가 올바르지 않습니다. " + e.getMessage());
        }

        return rowCnt;
    }

    public int remove(String id) {
        if (findById(id) == null) {
            throw new UserNotFoundException("해당 " + id + "를 가진 사용자를 찾을 수 없습니다.");
        }

        int rowCnt = 0;
        rowCnt = userDao.deleteById(id);
        if (rowCnt != 1) {
            throw new InternalServerError("DB에 정상적으로 반영도지 못했습니다. 현재 적용된 로우수는 " + rowCnt + "입니다.");
        }


        return rowCnt;
    }

    @Transactional(rollbackFor = Exception.class)
    public int removeAll() {
        int totalCnt = count();
        int rowCnt = 0;

        rowCnt = userDao.deleteAll();
        if (rowCnt == totalCnt) {
            throw new InternalServerError("DB에 정상적으로 반영도지 못했습니다. 현재 적용된 로우수는 " + rowCnt + "입니다.");
        }

        return rowCnt;
    }

}
