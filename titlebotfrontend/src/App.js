import logo from "./logo.svg";
import "./App.css";
import React, { useEffect, useState } from "react";
import Button from "react-bootstrap/Button";
import { Form, ListGroup, Image } from "react-bootstrap";

function App() {
  const [titleInfo, setTitleInfo] = useState([]);
  const [url, setUrl] = useState("");

  const fetchTitleInfoData = (url) => {
    console.log("hello");
    fetch("http://localhost:8084/title/" + url, {
      method: "GET",
      headers: {
        Accept: "*/*",
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        console.log(data);
        setTitleInfo((currentTitles) => [...currentTitles, data]);
      });
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    fetchTitleInfoData(url);
    setUrl("");
  };

  return (
    <div className='App'>
      <header className='App-header'>
        <Form onSubmit={handleSubmit}>
          <Form.Group>
            <Form.Label>URL</Form.Label>
            <Form.Control
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              placeholder='Enter Your URL Here'></Form.Control>
          </Form.Group>
          <Button variant='primary' type='submit'>
            Submit
          </Button>
        </Form>

        <div>
          {titleInfo.length > 0 && (
            <ListGroup variant='flush'>
              {titleInfo.map((title) => (
                <ListGroup.Item>
                  <Image src={title.faviconUrl}></Image> {title.title}
                </ListGroup.Item>
              ))}
            </ListGroup>
          )}
        </div>
      </header>
    </div>
  );
}

export default App;
