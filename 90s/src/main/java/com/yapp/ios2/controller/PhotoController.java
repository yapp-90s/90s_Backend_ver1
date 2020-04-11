package com.yapp.ios2.controller;

import com.yapp.ios2.service.IPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class PhotoController {

    @Autowired
    private IPhotoService photoService;

    @GetMapping("/photo")
    @ResponseBody
    public String home(){
        return "WELCOME, HERE IS PHOTO HOME";
    }


    @PostMapping(value = "/photo/upload")
    @ResponseBody
    public String upload(@RequestParam(value="image") MultipartFile image, @RequestParam("albumUid") Integer albumUid, @RequestParam("photoOrder") Integer photoOrder, @RequestParam("uploader") Integer uploader) throws IOException {
        String url = photoService.upload(image, albumUid, photoOrder, uploader);

        return url;
    }

    @PostMapping(value = "/photo/download")
    @ResponseBody
    public byte[] download(@RequestBody Map<String, String> json) throws IOException {

        Long album = Long.parseLong(json.get("album"));
        Long photo = Long.parseLong(json.get("photo"));

        byte[] file = photoService.download(album, photo);

        return file;
    }

}
