package com.stuart.hackatonproject.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.activity.ListFriendsActivity;
import com.stuart.hackatonproject.model.ReminderDB;
import com.stuart.hackatonproject.model.UserDB;
import com.stuart.hackatonproject.util.FirebaseUtils;
import com.stuart.hackatonproject.util.GenericFileProvider;
import com.stuart.hackatonproject.util.ImageUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.stuart.hackatonproject.util.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;

/**
 * Created by nathan on 10/11/17.
 */
@RuntimePermissions
public class DetailReminderFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "DetailReminderFragment";
    public static final String EXTRA_REMINDER = "EXTRA_REMINDER";
    private static final int REQUEST_CODE_GET_LIST_FRIEND = 3;
    private static final int CAMERA_REQUEST = 192;
    private ImageView imageViewAttachment1, imageViewAttachment2;
    private Uri imageToUploadUri;
    private String authority;
    File currentImageFile;
    private StorageReference child;

    public static Fragment instance(Context context) {
        return new DetailReminderFragment();
    }

    private TextView titleTextView;
    private TextView contentTextView;
    private TextView reminderAtTextView;
    private ArrayList<String> imagesLocations = new ArrayList<>();
    private ArrayList<File> localImageLocation = new ArrayList<>();
    private SparseArrayCompat<StorageReference> storageCompat = new SparseArrayCompat<>();
    private TextView friendTextList;
    private View contentLabelFriendList;


    FirebaseStorage storage = FirebaseStorage.getInstance();

    private ReminderDB reminderDB;
    private int imageSelection = 0;
    private boolean isImageExists = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reminderDB = getActivity().getIntent().getParcelableExtra(EXTRA_REMINDER);
        if (reminderDB == null) {
            reminderDB = new ReminderDB();
            reminderDB.generateUniqueId(); // this generate id even not used
            reminderDB.setFromUserId(FirebaseUtils.getCurrentUniqueUserId());
            isImageExists = false;
        } else {
            isImageExists = true;
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_detail, container, false);
        titleTextView = view.findViewById(R.id.edit_text_title);
        contentTextView = view.findViewById(R.id.edit_text_content);
        reminderAtTextView = view.findViewById(R.id.edit_text_reminder_at_time);
        reminderAtTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar notifyAtCalendar = Calendar.getInstance();
                if (reminderDB.getNotifyAt() <= 0) {
                    reminderDB.setNotifyAt(System.currentTimeMillis());
                    notifyAtCalendar.setTimeInMillis(reminderDB.getNotifyAt());
                    notifyAtCalendar.add(Calendar.DATE, 1);
                } else {
                    notifyAtCalendar.setTimeInMillis(reminderDB.getNotifyAt());
                }
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        DetailReminderFragment.this,
                        notifyAtCalendar.get(Calendar.YEAR),
                        notifyAtCalendar.get(Calendar.MONTH),
                        notifyAtCalendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show(getActivity().getFragmentManager(), "Datepickerdialog");

            }
        });
        friendTextList = view.findViewById(R.id.content_text_view);
        contentLabelFriendList = view.findViewById(R.id.content_label_view);
        contentLabelFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ListFriendsActivity.createIntent(getActivity());
                startActivityForResult(intent, REQUEST_CODE_GET_LIST_FRIEND);
            }
        });
        setAuthority();
        initImageUI(view);
        initImageDependencies();
        loadData();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_done, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                saveData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initImageDependencies() {
        StorageReference reference = storage.getReference();
        for (int i = 0; i < 2; i++) {
            String localFileName = String.format("real_image_%s_%d.jpg", reminderDB.getUniqueId(), i);
            reminderDB.put(i, localFileName);

            localImageLocation.add(new File(Environment.getExternalStorageDirectory(), localFileName));

            storageCompat.put(i, reference.child(localFileName));
        }
    }

    private void loadData() {
        if (reminderDB != null) {
            titleTextView.setText(reminderDB.getTitle());
            contentTextView.setText(reminderDB.getContent());

            for (int i = 0; i < storageCompat.size(); i++) {
                final int index = i;
                storageCompat.get(i).getFile(localImageLocation.get(i)).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        Log.d(TAG, "sudah selesai download " + taskSnapshot.getBytesTransferred() + " !!!! ");

                        switch (index) {
                            case 0:
                                Glide.with(DetailReminderFragment.this.getActivity())
                                        .asBitmap()
                                        .load(localImageLocation.get(index))
                                        .into(imageViewAttachment1);
                                break;
                            default:
                            case 1:
                                Glide.with(DetailReminderFragment.this.getActivity())
                                        .asBitmap()
                                        .load(localImageLocation.get(index))
                                        .into(imageViewAttachment2);
                                break;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        }
    }

    private void saveData() {
        String title = titleTextView.getText().toString();
        if (TextUtils.isEmpty(title)) {
            ToastUtil.showToast(getContext(), getString(R.string.title_must_be_filled));
            return;
        }

        String description = contentTextView.getText().toString();
        if (TextUtils.isEmpty(description)) {
            ToastUtil.showToast(getContext(), getString(R.string.description_must_be_filled));
            return;
        }
        reminderDB.setTitle(title);
        reminderDB.setContent(description);
        reminderDB.setCreatedAt(System.currentTimeMillis());
        List<String> toUserList = new ArrayList<>();
        toUserList.add(FirebaseUtils.getCurrentUniqueUserId());
        for (String toUser : toUserList) {
            sendTo(toUser);
        }
        ToastUtil.showToast(getContext(), getString(R.string.reminder_has_been_created));
        getActivity().finish();
    }

    private void sendTo(String toUserId) {
        reminderDB.setToUserId(toUserId);
        Trace trace = FirebasePerformance.getInstance().newTrace("trace_save_reminder");
        trace.start();
        reminderDB.save();
        trace.incrementCounter("save_reminder_hit");
        trace.stop();
    }

    private void initImageUI(View view) {
        imageViewAttachment1 = view.findViewById(R.id.image_view_image_attachment_1);
        imageViewAttachment2 = view.findViewById(R.id.image_view_image_attachment_2);
        imageViewAttachment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSelection = 0;
                DetailReminderFragmentPermissionsDispatcher.readAndWriteStorageWithPermissionCheck(DetailReminderFragment.this);
            }
        });
        imageViewAttachment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSelection = 1;
                DetailReminderFragmentPermissionsDispatcher.readAndWriteStorageWithPermissionCheck(DetailReminderFragment.this);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DetailReminderFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    void readAndWriteStorage() {
        DetailReminderFragmentPermissionsDispatcher.showCameraWithPermissionCheck(DetailReminderFragment.this);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void showCamera() {

        Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        chooserIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        switch (imageSelection) {
            case 0:
            case 1:
                currentImageFile = localImageLocation.get(imageSelection);
                break;
            default:
                return;
        }
        chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriForFile(getContext(), authority, currentImageFile));
        imageToUploadUri = GenericFileProvider.getUriForFile(getContext(), authority, currentImageFile);
        chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(chooserIntent, CAMERA_REQUEST);
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(final PermissionRequest request) {
//        new AlertDialog.Builder(this)
//                .setMessage(R.string.permission_camera_rationale)
//                .setPositiveButton(R.string.button_allow, (dialog, button) -> request.proceed())
//                .setNegativeButton(R.string.button_deny, (dialog, button) -> request.cancel())
//                .show();
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showDeniedForCamera() {
//        Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void showNeverAskForCamera() {
//        Toast.makeText(this, R.string.permission_camera_neverask, Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_GET_LIST_FRIEND) {
            UserDB userDB = data.getParcelableExtra(ListFriendsFragment.EXTRA_USER_CHOSEN);
            friendTextList.setText(userDB.getName());
            friendTextList.setVisibility(View.VISIBLE);
            return;
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (imageToUploadUri != null) {
                Uri selectedImage = imageToUploadUri;
                getActivity().getContentResolver().notifyChange(selectedImage, null);
                Bitmap reducedSizeBitmap = ImageUtils.getBitmap(getActivity(), authority, currentImageFile.getPath());
                if (reducedSizeBitmap != null) {
                    switch (imageSelection) {
                        case 0:
                            imageViewAttachment1.setImageBitmap(reducedSizeBitmap);
                            break;
                        default:
                        case 1:
                            imageViewAttachment2.setImageBitmap(reducedSizeBitmap);
                            break;
                    }

                    try {
                        InputStream stream = new FileInputStream(currentImageFile);
                        UploadTask uploadTask = storageCompat.get(imageSelection).putStream(stream);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                if (downloadUrl != null)
                                    imagesLocations.add(downloadUrl.toString());
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error while capturing Image", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "Error while capturing Image", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void setAuthority() {
        authority = getContext().getApplicationContext().getPackageName() + ".util.GenericFileProvider";
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(reminderDB.getNotifyAt());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DATE, dayOfMonth);
        reminderDB.setNotifyAt(calendar.getTimeInMillis());
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                DetailReminderFragment.this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.show(getActivity().getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(reminderDB.getNotifyAt());
        calendar.set(Calendar.HOUR, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        reminderDB.setNotifyAt(calendar.getTimeInMillis());
        String timeFormat = DateTimeUtils.formatTime(new Date(reminderDB.getNotifyAt()), true);
        reminderAtTextView.setText(timeFormat);
    }
}
