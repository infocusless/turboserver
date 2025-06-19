package com.trubochisty.truboserver.controller;

import com.trubochisty.truboserver.DTO.*;
import com.trubochisty.truboserver.model.Culvert;
import com.trubochisty.truboserver.service.CulvertService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * REST для управления {@link Culvert}.
 *
 */
@RestController
@RequestMapping("/culvert")
@RequiredArgsConstructor
public class CulvertController {

    private final CulvertService culvertService;

    /**
     * test
     *
     * @return строка "Culvert API is up"
     */
    @GetMapping("/test")
    public String test() {
        return "Culvert API test";
    }

    /**
     * Получить объект {@link Culvert} по id.
     *
     * @param id уникальный идентификатор трубы
     * @return объект Culvert, если найден
     */
    @GetMapping("/culverts/{id}")
    public ResponseEntity<CulvertDetailDTO> getCulvert(@PathVariable String id) {
        Culvert culvert = culvertService.getCulvert(id);
        return ResponseEntity.ok(CulvertMapper.mapToCulvertDetailDTO(culvert));
    }

    /**
     * Список всех {@link Culvert}.
     *
     * @return список всех труб
     */
    @GetMapping
    public ResponseEntity<List<Culvert>> getAllCulverts() {
        return ResponseEntity.ok(culvertService.getAllCulverts());
    }

    /**
     * Найти объекты {@link Culvert} по адресу.
     *
     * @param address часть адреса для поиска
     * @return список совпадающих труб
     */
    @GetMapping("/by-address")
    public ResponseEntity<List<Culvert>> getByAddress(@RequestParam String address) {
        return ResponseEntity.ok(culvertService.getCulvertsByAddress(address));
    }

    /**
     * Создать новый {@link Culvert}.
     *
     * @param culvertDTO объект, который нужно сохранить
     * @param photos - фотографии
     * @return созданный объект с присвоенным ID
     */
    @PostMapping
    public ResponseEntity<CulvertDetailDTO> createCulvert(
            @RequestPart("culvert") @Valid CulvertCreateDTO culvertDTO,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

        Culvert created = culvertService.createCulvert(culvertDTO, photos);
        return ResponseEntity.ok(CulvertMapper.mapToCulvertDetailDTO(created));
    }

    /**
     * Обновить существующий {@link Culvert} по id.
     *
     * @param id      ID объекта, который нужно обновить
     * @param culvert новые данные
     * @return обновлённый объект
     */
    @PutMapping("/{id}")
    public ResponseEntity<Culvert> updateCulvert(@PathVariable String id, @RequestBody CulvertUpdateDTO culvert) {
        try{
            Culvert updated = culvertService.updateCulvert(id, culvert);
            return ResponseEntity.ok(updated);
        }
        catch (Exception e) {
            //по идее лучше кастом написать
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Удалить {@link Culvert} по id.
     *
     * @param id, который нужно удалить
     * @return HTTP 204 No Content, если успешно
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCulvert(@PathVariable String id) {
        try{


            culvertService.deleteCulvert(id);
            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            //по идее лучше кастом написать
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/photos/{filename:.+}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String filename) {
        try {
            Path file = Paths.get("uploads").resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // или динамически через Files.probeContentType
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
