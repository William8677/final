// StoryActivity.kt: Actividad para gestionar la creación y visualización de historias
package com.williamfq.xhat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.content.Intent
import android.provider.MediaStore
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.williamfq.xhat.R

class StoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        val addStoryButton: Button = findViewById(R.id.addStoryButton)
        addStoryButton.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            val selectedImage: Uri? = data?.data
            selectedImage?.let { uploadToFirebase(it) }
        }
    }

    private fun uploadToFirebase(imageUri: Uri) {
        val storageReference = FirebaseStorage.getInstance().reference.child("stories/${System.currentTimeMillis()}.jpg")
        val storyId = FirebaseFirestore.getInstance().collection("stories").document().id

        storageReference.putFile(imageUri)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    saveStoryToFirestore(uri.toString(), storyId)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al subir la historia", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveStoryToFirestore(downloadUrl: String, storyId: String) {
        val storyData = hashMapOf(
            "storyId" to storyId,
            "imageUrl" to downloadUrl,
            "timestamp" to System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance().collection("stories").document(storyId)
            .set(storyData)
            .addOnSuccessListener {
                Toast.makeText(this, "Historia subida correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar la historia", Toast.LENGTH_SHORT).show()
            }
    }
}

