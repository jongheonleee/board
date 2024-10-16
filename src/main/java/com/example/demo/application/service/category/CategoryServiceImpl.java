package com.example.demo.application.service.category;

import com.example.demo.repository.mybatis.category.CategoryDaoImpl;
import com.example.demo.dto.category.CategoryDto;
import com.example.demo.application.exception.category.CategoryAlreadyExistsException;
import com.example.demo.application.exception.category.CategoryFormInvalidException;
import com.example.demo.application.exception.category.CategoryNotFoundException;
import com.example.demo.application.exception.global.InternalServerError;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl {

    private CategoryDaoImpl categoryDao;

    @Autowired
    public CategoryServiceImpl(CategoryDaoImpl categoryRepository) {
        this.categoryDao = categoryRepository;
    }

    public int count() {
        try {
            return categoryDao.count();
        } catch (Exception e) {
            throw new InternalServerError("예기치 못한 에러");
        }
    }

    public void create(CategoryDto dto) {
        try {
            int rowCnt = categoryDao.insert(dto);
            if (rowCnt != 1) {
                throw new InternalServerError("DB에 정상적으로 반영되지 못했습니다. 현재 적용된 로우수는 " + rowCnt + "입니다.");
            }
        } catch (DuplicateKeyException e) {
            throw new CategoryAlreadyExistsException("이미 존재하는 카테고리입니다.");
        } catch (DataIntegrityViolationException e) {
            throw new CategoryFormInvalidException("카테고리 데이터 입력 형식이 잘못되었습니다.");
        }
    }

    public CategoryDto findByCode(String code) {
        var found = categoryDao.selectByCode(code);

        if (found == null) {
            throw new CategoryNotFoundException("해당 " + code + "를 가진 카테고리를 찾을 수 없습니다.");
        }

        return found;
    }

    public List<CategoryDto> findAll() {
        return categoryDao.selectAll();
    }

    public void modify(CategoryDto dto) {
        try {
            int rowCnt = categoryDao.update(dto);
            if (rowCnt != 1) {
                throw new InternalServerError("DB에 정상적으로 반영되지 못했습니다. 현재 적용된 로우수는 " + rowCnt + "입니다.");
            }
        } catch (DuplicateKeyException e) {
            throw new CategoryAlreadyExistsException("이미 존재하는 카테고리입니다.");
        } catch (DataIntegrityViolationException e) {
            throw new CategoryFormInvalidException("카테고리 데이터 입력 형식이 잘못되었습니다.");
        }
    }

    public void remove(String code) {
        if (findByCode(code) == null) {
            throw new CategoryNotFoundException("해당 " + code + "를 가진 카테고리를 찾을 수 없습니다.");
        }

        int rowCnt = categoryDao.deleteByCode(code);

        if (rowCnt != 1) {
            throw new InternalServerError("DB에 정상적으로 반영되지 못했습니다. 현재 적용된 로우수는 " + rowCnt + "입니다.");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeAll() {
        int totalCnt = count();
        int rowCnt = categoryDao.deleteAll();

        if (totalCnt != rowCnt) {
            throw new InternalServerError("DB에 정상적으로 반영되지 못했습니다. 현재 적용된 로우수는 " + rowCnt + "입니다.");
        }
    }

    public List<CategoryDto> findAllTopCategory() {
        return categoryDao.selectAllTopCategory();
    }

    public List<CategoryDto> findAllByTopCate(String top_cate) {
        return categoryDao.selectAllByTopCate(top_cate);
    }

    public List<CategoryDto> findTopAndSubCategory() {
        // 대분류의 카테고리를 조회한다
        // 대분류 카테고리와 연관덴 중분류 카테고리를 조회한다
        // dto에 담아서 리턴한다
        List<CategoryDto> categories = categoryDao.selectAllTopCategory();
        for (var topCategory : categories) {
            String topCate = topCategory.getCate_code();
            List<CategoryDto> subCategories = categoryDao.selectAllByTopCate(topCate);
            topCategory.setSubCategories(subCategories);
        }

        return categories;

    }



}
