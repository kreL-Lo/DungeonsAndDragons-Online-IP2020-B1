﻿using Photon.Pun;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
public class RoomButton : MonoBehaviour
{

    public Text nameText;
    public Text sizeText;

    public string roomName;
    public int roomSize;

    public void SetRoom()
    {
        nameText.text = roomName;
        sizeText.text = roomSize.ToString();
    }

    public void JoinRoomOnclick()
    {
        PhotonNetwork.JoinRoom(roomName);
        SceneManager.LoadScene("LobbyScene");
    }
}
