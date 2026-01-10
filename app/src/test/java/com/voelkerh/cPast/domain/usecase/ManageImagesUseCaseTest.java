package com.voelkerh.cPast.domain.usecase;

import com.voelkerh.cPast.domain.repository.ImageRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ManageImagesUseCaseTest {

    @Test
    void manageImageUseCase_constructorNotNull() {
        ImageRepository imageRepository = mock(ImageRepository.class);

        ManageImagesUseCase manageImagesUseCase = new ManageImagesUseCase(imageRepository);

        assertNotNull(manageImagesUseCase);
    }

    @Test
    void getTempImmageData_callsImageRepository() {
        ImageRepository imageRepository = mock(ImageRepository.class);
        ManageImagesUseCase manageImagesUseCase = new ManageImagesUseCase(imageRepository);

        manageImagesUseCase.getTempImageData();

        verify(imageRepository, times(1)).getTempImageData();
    }

    @Test
    void saveImageToGallery_returnsFalsWhenImageFileNameIsNull() {
        ImageRepository imageRepository = mock(ImageRepository.class);
        ManageImagesUseCase manageImagesUseCase = new ManageImagesUseCase(imageRepository);

        boolean actual = manageImagesUseCase.saveImageToGallery(null, "note", "path");

        assertFalse(actual);
    }

    @Test
    void saveImageToGallery_returnsFalsWhenImageFileNameIsEmpty() {
        ImageRepository imageRepository = mock(ImageRepository.class);
        ManageImagesUseCase manageImagesUseCase = new ManageImagesUseCase(imageRepository);

        boolean actual = manageImagesUseCase.saveImageToGallery("", "note", "path");

        assertFalse(actual);
    }

    @Test
    void saveImageToGallery_returnsFalsWhenPhotoPathIsEmpty() {
        ImageRepository imageRepository = mock(ImageRepository.class);
        ManageImagesUseCase manageImagesUseCase = new ManageImagesUseCase(imageRepository);

        boolean actual = manageImagesUseCase.saveImageToGallery("file.jpg", "note", "");

        assertFalse(actual);
    }

    @Test
    void saveImageToGallery_returnsFalsWhenPhotoPathIsNull() {
        ImageRepository imageRepository = mock(ImageRepository.class);
        ManageImagesUseCase manageImagesUseCase = new ManageImagesUseCase(imageRepository);

        boolean actual = manageImagesUseCase.saveImageToGallery("file.jpg", "note", null);

        assertFalse(actual);
    }

    @Test
    void saveImageToGallery_returnsFalsWhenNoteIsNull() {
        ImageRepository imageRepository = mock(ImageRepository.class);
        ManageImagesUseCase manageImagesUseCase = new ManageImagesUseCase(imageRepository);

        boolean actual = manageImagesUseCase.saveImageToGallery("file.jpg", null, "path");

        assertFalse(actual);
    }

    @Test
    void saveImageToGallery_callsImageRepository() {
        ImageRepository imageRepository = mock(ImageRepository.class);
        ManageImagesUseCase manageImagesUseCase = new ManageImagesUseCase(imageRepository);

        manageImagesUseCase.saveImageToGallery("file.jpg", "note", "path");

        verify(imageRepository, times(1)).save(any(), any(), any(), any());
    }

    @Test
    void getHighestCounterForRecord_returnsZeroWhenBaseReferenceIsNull() {
        ImageRepository imageRepository = mock(ImageRepository.class);
        ManageImagesUseCase manageImagesUseCase = new ManageImagesUseCase(imageRepository);

        int actual = manageImagesUseCase.getHighestCounterForRecord(null);
        int expected = 0;

        assertEquals(expected, actual);
    }

    @Test
    void getHighestCounterForRecord_callsImageRepositoryWhenBaseReferenceValid() {
        ImageRepository imageRepository = mock(ImageRepository.class);
        ManageImagesUseCase manageImagesUseCase = new ManageImagesUseCase(imageRepository);
        String baseReference = "BArch_DQ1_16416";

        manageImagesUseCase.getHighestCounterForRecord(baseReference);

        verify(imageRepository, times(1)).getHighestCounterForRecord(any());
    }
}
