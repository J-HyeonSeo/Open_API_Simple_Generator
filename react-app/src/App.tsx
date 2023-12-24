import React from 'react';
import {Routes, Route} from "react-router-dom";
import MainPage from "./pages/MainPage";
import MyPage from "./pages/MyPage";
import GradePaymentPage from "./pages/GradePaymentPage";
import ApiIntroducePage from "./pages/ApiIntroducePage";
import ApiManagePage from "./pages/ApiManagePage";
import ApiCreatePage from "./pages/ApiCreatePage";
import LoginPage from "./pages/LoginPage";

function App() {
  return (
    <div>
      <Routes>
        <Route path="/" element={<MainPage/>}/>
        <Route path="/my-page" element={<MyPage/>}/>
        <Route path="/grade-payment" element={<GradePaymentPage/>}/>
        <Route path="/api" element={<ApiIntroducePage/>}/>
        <Route path="/api/manage" element={<ApiManagePage/>}/>
        <Route path="/api/create" element={<ApiCreatePage/>}/>
        <Route path="/login" element={<LoginPage/>}/>
      </Routes>
    </div>
  );
}

export default App;
