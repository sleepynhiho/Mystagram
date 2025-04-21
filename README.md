# Mystagram

Mystagram is an Instagram clone app built with [Jetpack Compose][compose] and Material3.

Some of the core features:
* Home Screen
* Post Reaction and Comment
* Story View
* Story Creation with Text and Main Palette
* Real-time Chat Box with Push Notifications
<p float="center">
  <img src="docs/home.gif" width="250" />
  <img src="docs/reaction.gif" width="250" />
  <img src="docs/story.gif" width="250" />
  <img src="docs/comment.gif" width="250" />
  <img src="docs/chat.gif" width="250" />
  <img src="docs/addStory.jpg" width="250" />
</p>

Note: 
1. To use the function of OAuth **login with Google** using firebase, you need to create an SHA1 key in the dir: **\.android\debug.keystore** 
by using this command line on Windows: **keytool -list -v -keystore <path\to\your\debug.keystore> -alias androiddebugkey -storepass android**
So normally the path to your debug.keystore would be something like:  **C:\Users\ADMIN\.android\debug.keystore**
<p float="center">
  <img src="docs/sha1_fix_bug_gg_login.png" width="250" />
</p>
2. Then on your project firebase, go to **Project settings** >> **Android apps** >> **Add fingerprint** >> Thêm phần SHA1 key lúc nãy tạo ra vào trong này:
<p float="center">
  <img src="docs/add_finger_print_login_gg.png" width="250" />
</p>
3. Chạy lại app để thấy kết quả.
Mystagram is still under development.
