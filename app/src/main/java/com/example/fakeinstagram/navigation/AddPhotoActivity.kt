package com.example.fakeinstagram.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fakeinstagram.R
import com.example.fakeinstagram.navigation.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //오픈앨범
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

        //업로드 이벤트
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM){
            if (resultCode == Activity.RESULT_OK){
                //업로드하는거
                photoUri = data?.data
                addphoto_image.setImageURI(photoUri)
            }else{
                //취소버튼 눌렀을때
                finish()
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    fun contentUpload(){
        //폴ㄹ더명
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //promise 방식
        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            var contentDTO = ContentDTO()

            //다운로드 url
            contentDTO.imageUrl = uri.toString()

            //유저 아이디
            contentDTO.uid = auth?.currentUser?.uid

            //유저아이디
            contentDTO.userId = auth?.currentUser?.email

            //콘텐트 설명
            contentDTO.explain = addphoto_edit_explain.text.toString()

            //타임스템프
            contentDTO.timestamp = System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(contentDTO)

            setResult(Activity.RESULT_OK)

            finish()
        }

        /*//콜백방식 파일업로드
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener{
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                var contentDTO = ContentDTO()

                //다운로드 url
                contentDTO.imageUrl = uri.toString()

                //유저 아이디
                contentDTO.uid = auth?.currentUser?.uid

                //유저아이디
                contentDTO.userId = auth?.currentUser?.email

                //콘텐트 설명
                contentDTO.explain = addphoto_edit_explain.text.toString()

                //타임스템프
                contentDTO.timestamp = System.currentTimeMillis()

                firestore?.collection("images")?.document()?.set(contentDTO)

                setResult(Activity.RESULT_OK)

                finish()

            }
        }*/
    }
}