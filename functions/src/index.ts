/**
 * Import function triggers from their respective submodules:
 *
 * import {onCall} from "firebase-functions/v2/https";
 * import {onDocumentWritten} from "firebase-functions/v2/firestore";
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

// import { onRequest } from "firebase-functions/v2/https";
// import * as logger from "firebase-functions/logger";

// Start writing functions
// https://firebase.google.com/docs/functions/typescript

// export const helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

// import * as functions from "firebase-functions";
// import * as admin from "firebase-admin";

// admin.initializeApp();

// Firebase Function to fetch users
// export const getUsers = functions.https.onRequest(async (req, res) => {
//   try {
//     const usersSnapshot = await admin.firestore().collection("users").get();
//     const usersList = usersSnapshot.docs.map((doc) => {
//       const userData = doc.data();
//       return {
//         userId: doc.id,
//         username: userData.username,
//         profileImage: userData.profileImage,
//       };
//     });

//     res.status(200).send(usersList);
//   } catch (error) {
//     res.status(500).send("Error fetching users: "
// + (error as Error).message);
//   }
// });

import * as functions from "firebase-functions/v1";
import * as admin from "firebase-admin";
import * as fs from "fs";
import * as path from "path";

const serviceAccount = JSON.parse(
  fs.readFileSync(path.join(__dirname, "service-account.json"), "utf8")
);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

exports.sendNotificationOnNewMessage = functions.firestore
  .document("chats/{chatId}/messages/{messageId}")
  .onCreate(async (snapshot, context) => {
    const messageData = snapshot.data();
    const chatId = context.params.chatId;

    // Lấy dữ liệu chat từ Firestore
    const chatDoc = await admin
      .firestore()
      .collection("chats")
      .doc(chatId)
      .get();
    if (!chatDoc.exists) return null;
    const chat = chatDoc.data();
    if (!chat) return null;

    const receiverId =
      chat.user1Id === messageData.senderId ? chat.user2Id : chat.user1Id;
    const senderImage =
      chat.user1Id === messageData.senderId
        ? chat.user1ProfileImage
        : chat.user2ProfileImage;

    // Lấy FCM Token của người nhận
    const receiverDoc = await admin
      .firestore()
      .collection("users")
      .doc(receiverId)
      .get();
    if (!receiverDoc.exists) return null;
    const receiverData = receiverDoc.data();
    if (!receiverData || !receiverData.fcmToken) return null;
    const fcmToken = receiverData.fcmToken;

    try {
      await admin.messaging().send({
        token: fcmToken,
        notification: {
          title: "New Message",
          body: messageData.content,
          imageUrl:
            senderImage && senderImage.startsWith("http")
              ? senderImage
              : "https://e7.pngegg.com/pngimages/84/165/png-clipart-united-states-avatar-organization-information-user-avatar-service-computer-wallpaper-thumbnail.png",
        },
        data: {
          chatId: chatId,
          senderId: messageData.senderId,
        },
      });
      console.log("🔔 Notification sent to:", receiverId);
    } catch (error) {
      console.error("❌ Error sending notification:", error);
    }
    return null;
  });
