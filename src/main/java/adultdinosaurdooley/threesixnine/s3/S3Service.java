package adultdinosaurdooley.threesixnine.s3;


import adultdinosaurdooley.threesixnine.admin.repository.ImageFileRepository;
import adultdinosaurdooley.threesixnine.product.entity.ProductEntity;
import adultdinosaurdooley.threesixnine.product.entity.ProductImageEntity;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3Client amazonS3Client;
    private final ImageFileRepository imageFileRepository;

    @Value("${cloud.aws.s3.product-bucket}")
    private String productBucket;

    //s3 올릴 이미지 객체 url로 변환 , DB 에 url 저장
    public void upload(List<MultipartFile> multipartFilelist, String dirName , ProductEntity product) throws IOException {
        int imageNum = 1; // 이미지 번호 초기값

        for (MultipartFile multipartFile : multipartFilelist){
            if (multipartFile != null){
                File uploadFile = convert(multipartFile)
                        .orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
                ProductImageEntity productImageEntity = new ProductImageEntity(upload(uploadFile, dirName), product); //url 정보 저장

                //이미지 번호 설정 1~ 이미지 갯수 만큼 ++
                productImageEntity.setImageNum(imageNum++);
                imageFileRepository.save(productImageEntity);
            }
        }
    }

    // S3로 파일 업로드하기
    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID(); // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(productBucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(productBucket, fileName).toString();
    }


    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("Local:File delete success");
            return;
        }
        log.info("Local: File delete fail");
    }

    //이미지 변환과정
    private Optional<File> convert(MultipartFile multipartFile) throws IOException {
//        File convertFile = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());
        File convertFile = new File(multipartFile.getOriginalFilename());
        // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    //s3 이미지 삭제
    public void deleteFile(String filename) {

        System.out.println("delete filename = " + filename);
        amazonS3Client.deleteObject(new DeleteObjectRequest(productBucket, filename));
        System.out.println(String.format("[%s] deletion complete", filename));

    }

}
