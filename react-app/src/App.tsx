import React from 'react';
import {Routes, Route} from "react-router-dom";
import MainPage from "./pages/MainPage";
import MyPage from "./pages/MyPage";
import GradePaymentPage from "./pages/GradePaymentPage";

function App() {
  return (
    <div>
      <Routes>
        <Route path="/" element={<MainPage/>}/>
        <Route path="/my-page" element={<MyPage/>}/>
        <Route path="/grade-payment" element={<GradePaymentPage/>}/>
      </Routes>
    </div>
  );
}

export default App;
