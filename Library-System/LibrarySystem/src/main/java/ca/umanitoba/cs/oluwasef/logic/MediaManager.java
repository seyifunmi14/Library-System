package ca.umanitoba.cs.oluwasef.logic;

import ca.umanitoba.cs.oluwasef.domain.*;
import ca.umanitoba.cs.oluwasef.exceptions.BorrowingException;
import ca.umanitoba.cs.oluwasef.exceptions.EntityNotFoundException;
import ca.umanitoba.cs.oluwasef.exceptions.InvalidReviewException;
import com.google.common.base.Preconditions;

import java.time.LocalDate;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * Logic manager for handling media transactions (borrowing and returning).
 */
public final class MediaManager {

    private final MemberManager memberManager;

    /**
     * Initializes the media manager.
     * @param memberManager the manager used to identify the current user. Must not be {@code null}.
     */
    public MediaManager(MemberManager memberManager) {
        this.memberManager = Preconditions.checkNotNull(memberManager);
    }

    private LibrarySystem system() {
        return memberManager.getSystem();
    }

    /**
     * Facilitates the borrowing of a media item by the current user.
     *
     * @param library the library where the media is located.
     * @param mediaId the unique identifier of the media to borrow.
     * @param today   the date the borrowing is taking place.
     * @throws BorrowingException      if there is no user currently logged in or the item cannot be borrowed.
     * @throws EntityNotFoundException if the media or library cannot be found.
     */
    public void borrowMedia(Library library,
                            String mediaId,
                            LocalDate today)
            throws BorrowingException, EntityNotFoundException {

        Member current = memberManager.getCurrentUser();
        if (current == null) {
            throw new BorrowingException();
        }

        system().borrowMedia(library, mediaId, current, today);
    }

    /**
     * Returns a media item and processes an optional review.
     *
     * @param library          the library where the item is being returned.
     * @param mediaId          the ID of the media being returned.
     * @param copyBarcode      the specific barcode of the physical copy.
     * @param today            the date the return is taking place.
     * @param ratingOrNull     an optional integer rating (1-5), or null if no rating.
     * @param reviewTextOrNull optional text review, or null if no review.
     * @throws BorrowingException      if the return cannot be processed.
     * @throws EntityNotFoundException if the current user or media cannot be identified.
     * @throws InvalidReviewException  if the provided rating or review text is invalid.
     */
    public void returnMedia(Library library,
                            String mediaId,
                            String copyBarcode,
                            LocalDate today,
                            Integer ratingOrNull,
                            String reviewTextOrNull)
            throws BorrowingException, EntityNotFoundException, InvalidReviewException {

        Member current = memberManager.getCurrentUser();

        if (current == null) {
            throw new EntityNotFoundException();
        }
        system().returnMedia(
                library,
                mediaId,
                copyBarcode,
                current,
                today,
                ratingOrNull,
                reviewTextOrNull
        );
    }
    /**
     * Simple helper record pairing a media item with a specific copy
     * that the member has checked out.
     */
    public static final class BorrowedItem {
        private final Media media;
        private final Copy copy;

        /**
         * Creates a new BorrowedItem pair.
         *
         * @param media the media definition. Must not be {@code null}.
         * @param copy  the specific physical copy. Must not be {@code null}.
         */
        public BorrowedItem(Media media, Copy copy) {
            this.media = Preconditions.checkNotNull(media);
            this.copy = Preconditions.checkNotNull(copy);
        }
        public Media getMedia() { return media;}

        public Copy getCopy() { return copy;}
    }

    /**
     * Finds all (Media, Copy) pairs in this library that are currently borrowed
     * by the given member.
     * @param library library to search
     * @param member  member whose borrowed items we want
     * @return a list of BorrowedItems found for the member.
     */
    public List<BorrowedItem> findBorrowedItemsForMember(Library library, Member member) {
        Preconditions.checkNotNull(library, "library must not be null");
        Preconditions.checkNotNull(member, "member must not be null");

        List<BorrowedItem> result = new ArrayList<>();

        for (Media media : library.getMedia()) {
            Collection<Copy> copies = media.getCopies();
            for (Copy c : copies) {
                Integer borrowerId = c.getBorrowerId();
                if (borrowerId != null && borrowerId.equals(member.getMemberId())) {
                    result.add(new BorrowedItem(media, c));
                }
            }
        }
        return result;
    }
}