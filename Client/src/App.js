import './App.css';
import React, {useEffect, useState} from "react";
import { Routes, Route } from "react-router-dom";
import { useNavigate } from "react-router-dom";

// PAGES
import Login from "./Pages/Credentials/Login";
import RegisterUsers from "./Pages/Credentials/RegisterUsers";
import Survey from "./Pages/Survey/Survey";
import WorkPlan from "./Pages/WorkPlan/WorkPlan";
import UsersManagement from "./Pages/UsersManagement/UsersManagement";
import InfoViewer from "./Pages/GeneralSupervisorInfoViewer/InfoViewer";
import GuidingBaskets from "./Pages/GuidingBaskets/GuidingBaskets";
import WorkReport from "./Pages/WorkReport/WorkReport";
import PasswordAuthentication from "./Pages/Credentials/PasswordAuthentication";
import GoalsManagement from "./Pages/GoalsManagement/GoalsManagement";
import ProfilePage from "./Pages/Profile/ProfilePage";
import SchoolsManagement from "./Pages/SchoolsManagement/SchoolsManagement";
import SurveyBuilder from "./Pages/SurveyBuilder/SurveyBuilder";

// COMPONENTS
import {
    AppBar, Backdrop, Box,
    Button,
    Drawer, IconButton,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Toolbar
} from "@mui/material";
import SurveyMenu from "./Pages/Survey/SurveyMenu";
import * as Space from 'react-spaces';
import Connection from "./Communication/Connection";

// ICONS
import HomeIcon from '@mui/icons-material/Home';
import PollIcon from '@mui/icons-material/Poll';
import ShoppingBasketIcon from '@mui/icons-material/ShoppingBasket';
import SummarizeIcon from '@mui/icons-material/Summarize';
import AccountCircle from "@mui/icons-material/AccountCircle";
import Typography from "@mui/material/Typography";
import TaskIcon from '@mui/icons-material/Task';
import MenuIcon from '@mui/icons-material/Menu';
import AccountBoxIcon from '@mui/icons-material/AccountBox';
import SchoolIcon from '@mui/icons-material/School';

// TODO: what to do if the request for info from the server to show fails?
// TODO: prevent users from going through the site by entering paths in the url
// TODO: currently saving everything in local storage but IT IS NOT SAFE
// TODO: save the state of the user between refreshes

// TODO: if there'll be time then add a loading animation to the different pages


function App(){
    // general state data
    const [type, setType] = useState("SUPERVISOR"); //TODO: change back to 'GUEST' when not developing window.sessionStorage.getItem('permission')
    const [openSidebar, setOpenSidebar] = useState(false);
    const [hideBars, setHideBars] = useState(false);
    const [openBackdrop, setOpenBackdrop] = useState(false);
    const [name, setName] = useState('');

    // authentication related
    const [authAvailability, setAuthAvailability] = useState(false);
    const [authCallback, setAuthCallback] = useState(() => () => {console.log("not auth callback")});
    const [authCalleePage, setAuthCalleePage] = useState(''); // todo: is there's a better way to do it? i do it that way cause i override the history stack and can't just go back
    const [authGoToPage, setAuthGoToPage] = useState('');

    let navigate = useNavigate();

    //let type = "SUPERVISOR";
    const barWidth = "10%";
    const sidebarWidth = "15%";
    const page_does_not_exist_string = "דף זה אינו קיים";
    const logout_button_string = "יציאה";
    // TODO: the greetings currently doesn't work well. but perhaps once TAL implements what i asked then it will (return the username with the response for the request)
    const greetings_string = "שלום " + name;

    // useEffect(() => {
    //     window.addEventListener('beforeunload', () => {
    //         handleLogout(); // TODO: refreshing causes it to trigger too
    //     });
    // }, [])

    /**
     * a callback to call when the result of the logout request got back
     * @param data the response for the logout request
     */
    const logoutCallback = (data) => {
        console.log(data)
        if(!data.failure){
            window.sessionStorage.removeItem('permission');
            setType(null);
            setHideBars(true);
            navigate('/user/login', {replace: true});
        }
    }

    /**
     * handler for when the logout button is pressed
     */
    const handleLogout = () => {
        new Connection().logout(logoutCallback);
    }

    // sidebar
    const drawer = (
        <Space.Fill>
            <Toolbar />
            {/*TODO: show buttons based on permissions*/}
            <List>
                {/*home button*/}
                <ListItem button onClick={
                    function () {
                        navigate(`user/home`, {replace: false});
                        setOpenSidebar(false);
                        setOpenBackdrop(false);
                    }}>
                    <ListItemIcon>
                        <HomeIcon/>
                    </ListItemIcon>
                    <ListItemText primary="בית"/>
                </ListItem>
                {/*profile button*/}
                <ListItem button onClick={
                    function () {
                        navigate(`user/profile`, {replace: false});
                        setOpenSidebar(false);
                        setOpenBackdrop(false);
                }}>
                    <ListItemIcon>
                        <AccountBoxIcon/>
                    </ListItemIcon>
                    <ListItemText primary="פרופיל"/>
                </ListItem>
                {/*survey button*/}
                <ListItem button onClick={
                    function () {
                        navigate(`survey/menu`, {replace: false});
                        setOpenSidebar(false);
                        setOpenBackdrop(false);
                    }}>
                    <ListItemIcon>
                        <PollIcon/>
                    </ListItemIcon>
                    <ListItemText primary="סקרים"/>
                </ListItem>
                {/*guiding baskets button*/}
                <ListItem button onClick={
                    function () {
                        navigate(`user/guidingBasketsSearch`, {replace: false});
                        setOpenSidebar(false);
                        setOpenBackdrop(false);
                    }}>
                    <ListItemIcon>
                        <ShoppingBasketIcon/>
                    </ListItemIcon>
                    <ListItemText primary="סלי הדרכה"/>
                </ListItem>
                {/*work report button*/}
                <ListItem button onClick={
                    function () {
                        navigate(`user/workReport`, {replace: false});
                        setOpenSidebar(false);
                        setOpenBackdrop(false);
                    }}>
                    <ListItemIcon>
                        <SummarizeIcon/>
                    </ListItemIcon>
                    <ListItemText primary='דו"ח עבודה'/>
                </ListItem>
                {/*goals management button*/}
                <ListItem button onClick={
                    function () {
                        navigate(`user/goalsManagement`, {replace: false});
                        setOpenSidebar(false);
                        setOpenBackdrop(false);
                    }}>
                    <ListItemIcon>
                        <TaskIcon/>
                    </ListItemIcon>
                    <ListItemText primary='ניהול יעדים'/>
                </ListItem>
                {/*schools button*/}
                <ListItem button onClick={
                    function () {
                        navigate(`user/schools`, {replace: false});
                        setOpenSidebar(false);
                        setOpenBackdrop(false);
                    }}>
                    <ListItemIcon>
                        <SchoolIcon/>
                    </ListItemIcon>
                    <ListItemText primary="בתי ספר"/>
                </ListItem>
            </List>
        </Space.Fill>
    );

    return (
        <div dir="rtl">
            <Space.ViewPort>
                {/* app bar */}
                {!hideBars && <Space.Top size={barWidth}>
                    <AppBar color="background" position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
                        <Toolbar>
                            {/*menu button*/}
                            <IconButton
                                color="inherit"
                                aria-label="Menu"
                                onClick={
                                    function () {
                                        setOpenSidebar(!openSidebar);
                                        setOpenBackdrop(!openBackdrop);
                                    }
                            }

                            >
                                <MenuIcon />
                            </IconButton>
                            {/*logo*/}
                            <Typography
                                variant="h6"
                                noWrap
                                component="div"
                                onClick={() => navigate(`user/home`, {replace: false})}
                            >
                                מענ"ה
                            </Typography>
                            {/*todo: see about the logo*/}
                            <Box
                                component="img"
                                sx={{
                                    height: 64,
                                }}
                                alt="Your logo."
                                src={"logo.svg"}
                            />
                            {/*profile button*/}
                            <IconButton
                                size="large"
                                aria-label="account of current user"
                                aria-controls="menu-appbar"
                                aria-haspopup="true"
                                color="inherit"
                                onClick={() => navigate(`user/profile`, {replace: false})}
                            >
                                <AccountCircle />
                            </IconButton>
                            {/*greetings text*/}
                            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                                {greetings_string}
                            </Typography>
                            {/*logout button*/}
                            <Button id="logout_button" onClick={() => handleLogout()} color="inherit">{logout_button_string}</Button>
                        </Toolbar>
                    </AppBar>
                    {/*sidebar*/}
                    <Drawer
                        sx={{
                            width: sidebarWidth,
                            flexShrink: 0,
                            '& .MuiDrawer-paper': {
                                width: sidebarWidth,
                                boxSizing: 'border-box',
                            },
                        }}
                        variant="persistent"
                        anchor="left"
                        open={openSidebar}
                    >
                        {drawer}
                    </Drawer>
                </Space.Top>}
                <Space.Fill>
                    {/* routes to the different screens */}
                    <Routes>
                        {/*TODO: find a more elegant way for the permissions*/}
                        <Route path="user">
                            <Route path="login" element={<Login type={type} setType={setType} setName={setName} setHideBars={setHideBars}/>}/>

                            {authAvailability && <Route path="auth" element={<PasswordAuthentication callback={authCallback} callee={authCalleePage} goto={authGoToPage} setHideBars={setHideBars}/>}/>}

                            <Route path="profile" element={<ProfilePage/>}/>

                            {/*todo: is this restriction is ok?*/}
                            {(type !== "GUEST") && <Route path="schools" element={<SchoolsManagement/>}/>}

                            {(type === "SUPERVISOR" || type === "INSTRUCTOR") &&
                                <Route path="guidingBasketsSearch" element={<GuidingBaskets/>}/>}

                            {(type === "SUPERVISOR" || type === "INSTRUCTOR") &&
                                <Route path="workReport" element={<WorkReport/>}/>}

                            {(type === "SUPERVISOR" || type === "SYSTEM_MANAGER") &&
                                <Route path="registerUsers" element={<RegisterUsers type={type}/>}/>}

                            {(type === "SUPERVISOR" || type === "SYSTEM_MANAGER") &&
                                <Route path="goalsManagement" element={<GoalsManagement/>}/>}}}

                            {(type === "SUPERVISOR" || type === "SYSTEM_MANAGER") &&
                                <Route path="home" element={<UsersManagement userType={type} setAuthAvailability={setAuthAvailability} setAuthCallBack={setAuthCallback} setAuthCalleePage={setAuthCalleePage} setAuthGoToPage={setAuthGoToPage} setHideBars={setHideBars}/>}/>}

                            {type === "INSTRUCTOR" &&
                                <Route path="home" element={<WorkPlan/>}/>}

                            {type === "GENERAL_SUPERVISOR" &&
                                <Route path="home" element={<InfoViewer/>}/>}

                        {/*TODO: maybe put survey inside user?*/}
                        </Route>

                        {type === "SUPERVISOR" &&
                            <Route path="survey">
                                <Route path="menu" element={<SurveyMenu />}/>
                                <Route path="createSurvey" element={<SurveyBuilder/>}/>
                                <Route path="getSurvey" element={<Survey/>}/>
                                {/*<Route path="surveyConstraint" element={<SurveyConstraintBuilder/>}/>*/}
                            </Route>}

                        <Route
                            path="*"
                            element={
                                <main style={{ padding: "1rem" }}> {/*todo: make this page prettier */}
                                    <p>{page_does_not_exist_string}</p>
                                </main>
                            }
                        />
                    </Routes>
                    <Backdrop open={openBackdrop}/>
                </Space.Fill>
            </Space.ViewPort>
        </div>
    )
}

export default App;

