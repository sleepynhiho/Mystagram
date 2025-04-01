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

import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

// Firebase Function to fetch users
export const getUsers = functions.https.onRequest(async (req, res) => {
  try {
    const usersSnapshot = await admin.firestore().collection("users").get();
    const usersList = usersSnapshot.docs.map((doc) => {
      const userData = doc.data();
      return {
        userId: doc.id,
        username: userData.username,
        profileImage: userData.profileImage,
      };
    });

    res.status(200).send(usersList);
  } catch (error) {
    res.status(500).send("Error fetching users: " + (error as Error).message);
  }
});
