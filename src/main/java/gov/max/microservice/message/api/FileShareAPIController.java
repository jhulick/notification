package gov.max.microservice.message.api;

import gov.max.microservice.message.repository.FileOwnerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gov.max.microservice.message.user.FileOwner;

import javax.validation.Valid;
import java.util.List;

/**
 * This class is used to call all the File Share API.
 */
@RestController
public class FileShareAPIController {

    @Autowired
    private FileOwnerRepo repo;

    private String errMsg;

    /**
     * Add Owner
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @RequestMapping(value = "/api/owners", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<FileOwner> addOwner(@Valid @RequestBody FileOwner owner) {
        if (owner.getName() == null || owner.getName().isEmpty()) {
            errMsg = "Name field cannot be empty. Please provide name";
            return new ResponseEntity(errMsg, HttpStatus.BAD_REQUEST);
        } else if (owner.getEmail() == null || owner.getEmail().isEmpty()) {
            errMsg = "Email field cannot be empty. Please provide email";
            return new ResponseEntity(errMsg, HttpStatus.BAD_REQUEST);
        } else {
            FileOwner addOwner = new FileOwner(owner.getName(), owner.getEmail());
            generateOwnerId(addOwner);
            repo.save(addOwner);
            return new ResponseEntity<>(addOwner, HttpStatus.CREATED);
        }
    }

    /**
     * View Owner
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "api/owners/{owner_id}", method = RequestMethod.GET)
    public ResponseEntity<FileOwner> viewOwner(@PathVariable("owner_id") Integer owner_id) {
        if (owner_id == null) {
            errMsg = "Owner Id not provided";
            return new ResponseEntity(errMsg, HttpStatus.BAD_REQUEST);
        } else {
            if (repo.findById(owner_id) == null) {
                return new ResponseEntity("Owner does not exist", HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(repo.findById(owner_id), HttpStatus.OK);
            }
        }
    }

    /**
     * Update Owner
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "api/owners/{owner_id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<FileOwner> updateOwner(@Valid @RequestBody FileOwner owner, @PathVariable("owner_id") Integer owner_id) {
        if (owner.getEmail() == null || owner.getEmail().isEmpty()) {
            errMsg = "Email field cannot be empty. Please provide email";
            return new ResponseEntity(errMsg, HttpStatus.BAD_REQUEST);
        } else if (repo.findById(owner_id) == null) {
            errMsg = "Moderator does not exist";
            return new ResponseEntity(errMsg, HttpStatus.BAD_REQUEST);
        } else {
            FileOwner updateOwner = repo.findById(owner_id);
            updateOwner.setEmail(owner.getEmail());
            updateOwner.setName(owner.getName());
            repo.save(updateOwner);
            return new ResponseEntity<>(updateOwner, HttpStatus.CREATED);
        }
    }

    /**
     * Generate Owner ID for new Owner
     */
    private void generateOwnerId(FileOwner mod) {
        List<FileOwner> checkEmpty = repo.findAll();
        if (checkEmpty.isEmpty()) {
            mod.setId(12345);
        } else {
            int id = checkEmpty.get(checkEmpty.size() - 1).getId();
            mod.setId(id + 1);
        }
    }
}
