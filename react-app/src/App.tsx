import React from 'react';
import {Routes, Route} from "react-router-dom";
import MainPage from "./pages/MainPage";
import MyPage from "./pages/MyPage";

function App() {
  return (
    <div>
      <Routes>
        <Route path="/" element={<MainPage/>}/>
        <Route path="/my-page" element={<MyPage/>}/>
      </Routes>
    </div>
  );
}

export default App;
