package gov.max.microservice.message.api;

import com.fasterxml.jackson.annotation.JsonView;

import gov.max.microservice.message.repository.FileOwnerRepo;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gov.max.microservice.message.user.FileOwner;
import gov.max.microservice.message.user.FileMetadata;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used to call all the File Share API.
 */

@RestController
public class FileOwnerDataController {

    private static final Logger logger = Logger.getLogger(FileOwnerDataController.class);

    private static ArrayList<FileMetadata> modFileMetadataList = null;
    private static ArrayList<String> msg;

    @Autowired
    private FileOwnerRepo ownerRepo;


    public FileOwnerDataController() {
        msg = new ArrayList<>();
    }

    /* Create New File Data in Owner List */

    @RequestMapping(value = "api/owners/{owner_id}/filedata", method = RequestMethod.POST)
    @JsonView(DisplayResult.withoutResults.class)
    public ResponseEntity<FileMetadata> createFileDataByOwnerId(@PathVariable("owner_id") Integer ownerId, @Valid @RequestBody FileMetadata fm) {
        FileOwner owner = ownerRepo.findById(ownerId);
        FileMetadata newFileMetadata = new FileMetadata(fm.getName(), fm.getStarted_at(), fm.getExpired_at());
        generateFileDataId(newFileMetadata, ownerId);
        owner.putFileDataInList(newFileMetadata);
        ownerRepo.save(owner);
        //  ownerRepo.save(newFileMetadata);
        return new ResponseEntity<>(newFileMetadata, HttpStatus.CREATED);
    }


	/* View File Data without Results */

    @SuppressWarnings({"unchecked", "rawtypes"})
    @RequestMapping(value = "api/filedata/{filedata_id}", method = RequestMethod.GET)
    @JsonView(DisplayResult.withoutResults.class)
    public ResponseEntity viewFileDataWithoutResult(@PathVariable("filedata_id") String fileDataId) {
        if (getFileMetadataFromOwner(fileDataId).isEmpty() || getFileMetadataFromOwner(fileDataId) == null) {
            return new ResponseEntity("File Data does not exist", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity(getFileMetadataFromOwner(fileDataId), HttpStatus.OK);
        }
    }


    /**
     * View File Data with Result
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "api/owners/{owner_id}/filedata/{filedata_id}", method = RequestMethod.GET)
    @JsonView(DisplayResult.withResults.class)
    public ResponseEntity viewFileDataWithResult(@PathVariable("filedata_id") String fileDataId, @PathVariable("owner_id") Integer modId) {
        FileOwner mod = ownerRepo.findById(modId);
        if (mod.getOwnerFileMetadata(fileDataId) == null) {
            return new ResponseEntity("File Data does not exist", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity(mod.getOwnerFileMetadata(fileDataId), HttpStatus.OK);
        }
    }

	/* List File Data */

    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "api/owners/{owner_id}/filedata", method = RequestMethod.GET)
    @JsonView(DisplayResult.withResults.class)
    public ResponseEntity<ArrayList<FileMetadata>> listAllFileDataByOwnerId(@PathVariable("owner_id") Integer modId) {
        FileOwner owner = ownerRepo.findById(modId);
        if (owner == null) {
            return new ResponseEntity("No file data created by this owner", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(owner.getFileMetadataList(), HttpStatus.OK);
        }
    }


	/* Delete File Data */

    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "api/owners/{owner_id}/filedata/{filedata_id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteFileData(@PathVariable("filedata_id") String fileDataId, @PathVariable("owner_id") Integer modId) {
        FileOwner owner = ownerRepo.findById(modId);
        owner.deleteOwnerFileMetadata(fileDataId);
        ownerRepo.save(owner);
        //  ownerRepo.delete(fileDataId);
        return new ResponseEntity("File Data Deleted", HttpStatus.NO_CONTENT);
    }


    /* Generate FileMetadata ID with a combination of owner_id and timestamp */

    private void generateFileDataId(FileMetadata fm, Integer ownerId) {
        Date dt = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddhhmmss");
        String time = f.format(dt);
        Long temp = ownerId + Long.valueOf(time);
        String s = Long.toUnsignedString(temp, 36).toUpperCase();
        fm.setId(s);
    }

    private List<FileMetadata> getFileMetadataFromOwner(String fileDataId) {
        List<FileMetadata> fileMetadataList = new ArrayList<>();
        List<FileOwner> ownerList = ownerRepo.findAll();
        for (FileOwner owner : ownerList) {
            if (owner.getOwnerFileMetadata(fileDataId) != null)
                fileMetadataList.add(owner.getOwnerFileMetadata(fileDataId));
        }
        return fileMetadataList;
    }

    public ConcurrentHashMap<String, ArrayList<String>> getExpiredFileMetadata() {
        List<FileOwner> ownerRepoAll = ownerRepo.findAll();
        ConcurrentHashMap<String, ArrayList<String>> allData = new ConcurrentHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            if (ownerRepoAll == null) {
                logger.info("Moderator returning null");
            } else {
                for (FileOwner owner : ownerRepoAll) {
                    String key = owner.getEmail();
                    ArrayList<String> tempMsg = new ArrayList<>();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
                    LocalDateTime ldt = LocalDateTime.now();
                    Date currentDate = sdf.parse(ldt.format(dtf));
                    if (owner.getFileMetadataList() != null) {
                        for (FileMetadata fm : owner.getFileMetadataList()) {
                            String time = (fm.getExpired_at().substring(0, 10)) + " " + (fm.getExpired_at().substring(11, 19));
                            Date expiredDate = sdf.parse(time);
                            if (expiredDate.before(currentDate) && fm.getFlag() == 0) {
                                fm.setFlag(1);
                                String temp = owner.getEmail() + ":File[" + fm.getName() + "] has expired";
                                tempMsg.add(temp);
                                ownerRepo.save(owner);
                            }
                        }
                        allData.put(key, tempMsg);
                    }
                }
            }
            for (HashMap.Entry<String, ArrayList<String>> entry : allData.entrySet()) {
                String key = entry.getKey();
                ArrayList<String> dataValue = entry.getValue();
                if (dataValue.isEmpty())
                    allData.remove(key, dataValue);
            }
        } catch (NullPointerException e) {
            logger.info(e.getMessage());
            logger.info("No File Metadata Found, NullPointException generated");
        } catch (IndexOutOfBoundsException e) {
            logger.info(e.getMessage());
            logger.info("IndexOutOfBoundsException");
        } catch (NoSuchElementException e) {
            logger.info(e.getMessage());
            logger.info("Error retrieving data, NoSuchElementException generated");
        } catch (ParseException e) {
            logger.info(e.getMessage());
            logger.info("Cannot parse date, ParseException generated");
        }
        return allData;
    }
}
