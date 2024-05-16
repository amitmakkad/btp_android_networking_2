## Overview
This app is used to ping a server using the Retrofit library. This is a dummy version to check how the Android app will send data to a server in which a deep learning model will run and return the response. Different methods explored:-

## Method 1: Calculate Avg pixel value from image
* In this method, we are using ngrok to generate a public URL for the server, but the issue persists for a maximum of 2 hours. So, to avoid changing the URL in the app again and again:
  - First, the app will ping [base_url](https://amitmakkad.github.io/btp_android_networking_1/) to find the server_base URL ('ngrok URL').
  - The server_base URL can be modified in the [base repo](https://github.com/amitmakkad/btp_android_networking_1).
  - Then, we can ping the server_base URL along with the attached image, and it will return a response.
* We can access the server without rebuilding even if its server_base URL keeps changing, and we don't have to change anything in the app.
  ![image](https://github.com/amitmakkad/btp_android_networking_2/assets/79632719/10d08a02-80c4-4eaf-aa04-15943723834b)

## Method 2: Calculate the shape of a CSV file
* The app will ping base_ip ('your server IP') along with the attached CSV file, and it will return a response.
* In this method, we can access the server directly, but the app and server should be connected to the same network.
