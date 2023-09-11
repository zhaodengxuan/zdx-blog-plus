package com.zdx.strategy;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface DataImportExportStrategy<T> {

    String type();


    void importData(MultipartFile file, IService<T> iService) throws IOException;

    void exportData(HttpServletResponse response, Wrapper<T> wrapper, IService<T> service) throws IOException;
}
