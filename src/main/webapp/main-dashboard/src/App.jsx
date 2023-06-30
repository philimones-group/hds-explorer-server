import {useEffect, useState} from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Sidebar from "./components/Sidebar";
import Home from "./pages/Home";

function App() {
  //collapse sidebar function that will be passed and triggered in child component, basically readjust the grid layout on main page (parent)
  const [collapse, setCollapse] = useState('sidebar-collapse')
  const [translations, setTranslations] = useState({});

  const changeLayout = (collapseState) => {
    setCollapse(collapseState)
  }

  //get app base url

function getBaseUrl() {
  let url = window.location.href; //it supposes to be $ANY_URL/dashboard/* -
  let last_index = url.lastIndexOf("/dashboard")
  last_index = last_index == -1 ? url.lastIndexOf("/") : last_index

  url = url.substring(0, last_index)

  console.info("url final: "+url)

  return url
}


  //get i18n messages from grails
  const fetchI18nMessages = async () => {
    const response = await fetch(getBaseUrl() + "/dashboard/i18nmessages")
    if (!response.ok) {
      console.error('Error fetching data of totals');
    } else {
      return response.json()
    }
  }

  useEffect(() => {
    fetchI18nMessages()
        .then((result) => {
          setTranslations(result)
        })
        .catch((error) => {
          console.log(error.message)
        })
  }, [])

  return (
    <>
    <BrowserRouter>
    <section className={`md:grid ${collapse} mx-2 md:mr-2 md:ml-0 gap-2 grid-rows-size`}>
      <div className="col-span-1 row-span-3"><Sidebar i18n={translations} layout={changeLayout}/></div>
      <section className="col-start-2 col-span-4 row-span-full">
        <br/>
        <Home baseUrl={getBaseUrl()} i18n={translations}/>
      </section>
    </section>
    </BrowserRouter>
    </>
  );
}

export default App;
