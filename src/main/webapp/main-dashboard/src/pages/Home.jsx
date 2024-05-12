import Card from "../components/Card"
import { HiOutlineLogout, HiOutlineLogin, HiUserGroup} from 'react-icons/hi'
import {AiOutlineUserAdd, AiOutlineUserDelete ,AiOutlineUserSwitch} from 'react-icons/ai'
import {BsHouseAdd, BsHouseAddFill, BsHouseCheck, BsPeople} from 'react-icons/bs'
import {MdExpandLess, MdExpandMore, MdOutlinePregnantWoman} from 'react-icons/md'
import {RiUserSharedFill, RiUserUnfollowFill} from 'react-icons/ri'
import {FaHouseUser} from 'react-icons/fa'
import {BiChild} from 'react-icons/bi'
import { useState, useEffect } from "react"
import ChartContainer from "../components/ChartContainer"
import ChartBar from "../components/ChartBar"
import { Table } from "../components/Table"
import { dataB, religionData, educationData } from "../../public/mockData"
import ChartPie from "../components/ChartPie"
import DonutChart from "../components/DonutChart"
import CardMain from "../components/CardMain"
import ChartPyramid from "../components/ChartPyramid"
import LineChartContainer from "../components/LineChartContainer"
import 'whatwg-fetch'
import Loading from "../components/Loading"

function Home({baseUrl, i18n}) {
  //State to control expand card area
  const [expandCardArea, setExpandCardArea] = useState(false)
  const [totals, setTotals] = useState(null)
  const [pyramidBars, setPyramidBars] = useState(null)
  const [fieldworkerStatus, setFieldworkerStatus] = useState(null)
  const [coreFormStatus, setCoreFormStatus] = useState(null)
  const [religionStatus, setReligionStatus] = useState(null)
  const [educationStatus, setEducationStatus] = useState(null)
  const [formData1, setFormData1] = useState(null) //region
  const [formData2, setFormData2] = useState(null) //household
  const [formData3, setFormData3] = useState(null) //visit
  const [formData4, setFormData4] = useState(null)
  const [formData5, setFormData5] = useState(null)
  const [formData6, setFormData6] = useState(null)
  const [formData7, setFormData7] = useState(null)
  const [formData8, setFormData8] = useState(null)
  const [formData9, setFormData9] = useState(null)
  const [formData10, setFormData10] = useState(null)
  const [formData11, setFormData11] = useState(null)
  const [formData12, setFormData12] = useState(null)
  const [formData13, setFormData13] = useState(null)
  const [loadingPyramid, setLoadingPyramid] = useState(false)
  const [loadingFieldWorker, setLoadingFieldWorker] = useState(true)
  const [loadingReligion, setLoadingReligion] = useState(true)
  const [loadingEducation, setLoadingEducation] = useState(true)


  //console.info(baseUrl + " - new mode running")

  //get fetched data - totals
  const fetchDataTotals = async () => {
      const response = await fetch(baseUrl + "/dashboard/totals")
      if (!response.ok) {
        console.error('Error fetching data of totals');
      } else {
        return response.json()
      }
  }

  //get fetched data - pyramid bars
  const fetchDataPyramidBars = async () => {
    setLoadingPyramid(true)
    const response = await fetch(baseUrl + "/dashboard/populationPyramid")
    if (!response.ok) {
        console.error('Error fetching population pyramid data');
    } else {
        return response.json()
    }
  }

  //get fetched data - fieldworker status
  const fetchDataFieldworkerStatus = async () => {
    const response = await fetch(baseUrl + "/dashboard/fieldworkerStatus")
    if (!response.ok) {
        console.error('Error fetching population fieldworker status data');
    } else {
        return response.json()
    }
  }

  //get fetched data - coreforms status
  const fetchDataCoreFormStatus = async () => {
    const response = await fetch(baseUrl + "/dashboard/coreFormStatus")
    if (!response.ok) {
        console.error('Error fetching population coreform status data');
    } else {
        return response.json()
    }
  }

  //get fetched data - religions status
  const fetchDataReligionStatus = async () => {
    const response = await fetch(baseUrl + "/dashboard/religions")
    if (!response.ok) {
        console.error('Error fetching population coreform status data');
    } else {
        return response.json()
    }
  }

  //get fetched data - educations status
  const fetchDataEducationStatus = async () => {
    const response = await fetch(baseUrl + "/dashboard/educations")
    if (!response.ok) {
        console.error('Error fetching population coreform status data');
    } else {
        return response.json()
    }
  }

  useEffect(() => {
    fetchDataTotals()
        .then((result) => {
          setTotals(result)
        })
        .catch((error) => {
          console.log(error.message)
        })
  }, [])

  useEffect(() => {
    fetchDataPyramidBars()
        .then((result) => {
            // Remove quotes from keys
            const formattedData = JSON.parse(JSON.stringify(result));
            //console.info(formattedData)
            setPyramidBars(formattedData)
            setLoadingPyramid(false)
        })
        .catch((error) => {
            console.log(error.message)
        })
  }, [])

  useEffect(() => {
    fetchDataFieldworkerStatus()
        .then((result) => {
            // Remove quotes from keys
            const formattedData = JSON.parse(JSON.stringify(result));
            //console.info(formattedData)
            setFieldworkerStatus(formattedData)
            setLoadingFieldWorker(false)
        })
        .catch((error) => {
            console.log(error.message)
        })
  }, [])

  useEffect(() => {
    fetchDataCoreFormStatus()
        .then((result) => {
            // Remove quotes from keys
            //const formattedData = JSON.parse(JSON.stringify(result));
            //console.info(formattedData)
            setCoreFormStatus(result)
            retrieveCoreFormsFromJson(result)
        })
        .catch((error) => {
            console.log(error.message)
        })
  }, [])


  const retrieveCoreFormsFromJson = (result) => {
      setFormData1(result["RawRegion"]);
      setFormData2(result["RawHousehold"]);
      setFormData3(result["RawVisit"]);
      setFormData4(result["RawMemberEnu"]);
      setFormData5(result["RawMaritalRelationship"]);
      setFormData6(result["RawInMigration"]);
      setFormData7(result["RawExternalInMigration"]);
      setFormData8(result["RawOutMigration"]);
      setFormData9(result["RawDeath"]);
      setFormData10(result["RawPregnancyRegistration"]);
      setFormData11(result["RawPregnancyOutcome"]);
      setFormData12(result["RawChangeHead"]);
      setFormData13(result["RawIncompleteVisit"]);
  }

    useEffect(() => {
    fetchDataReligionStatus()
        .then((result) => {
            // Remove quotes from keys
            const formattedData = JSON.parse(JSON.stringify(result));
            //console.info(formattedData)
            setReligionStatus(formattedData)
            setLoadingReligion(false)
        })
        .catch((error) => {
            console.log(error.message)
        })
  }, [])

    useEffect(() => {
    fetchDataEducationStatus()
        .then((result) => {
            // Remove quotes from keys
            const formattedData = JSON.parse(JSON.stringify(result));
            //console.info(formattedData)
            setEducationStatus(formattedData)
            setLoadingEducation(false)
        })
        .catch((error) => {
            console.log(error.message)
        })
  }, [])

  return (
      <section className="px-5">
          <div className="cards block-1">
              <h3 className="text-title-alt text-lg font-semibold">{i18n['dashboard.greeting']}</h3>
              <section className="grid gap-5 md:w-fit xl:w-full md:grid-cols-2 xl:flex xl:justify-between xl:mx-auto max-w-[2400px]">
                  <CardMain i18n={i18n} label={i18n['dashboard.totalHouseholds']} value={totals?.households} icon={<BsHouseAddFill className="text-[25px] font-thin "/>} iconBg={'bg-costume-dark'} genderStatus={false} maleCount={0} femaleCount={0} />
                  <CardMain i18n={i18n} label={i18n['dashboard.totalIndividuals']} value={totals?.individuals} icon={<HiUserGroup className="text-[25px] font-thin "/>} iconBg={'bg-costume-blue'} genderStatus={true} maleCount={totals?.individuals_male} femaleCount={totals?.individuals_female} />
                  <CardMain i18n={i18n} label={i18n['dashboard.totalResidents']} value={totals?.residents} icon={<FaHouseUser className="text-[25px] font-thin "/>} iconBg={'bg-costume-green'} genderStatus={true} maleCount={totals?.residents_male} femaleCount={totals?.residents_female} />
                  <CardMain i18n={i18n} label={i18n['dashboard.totalOutmigrated']} value={totals?.outmigrations} icon={<RiUserSharedFill className="text-[25px] font-thin "/>} iconBg={'bg-costume-pink'} genderStatus={true}  maleCount={totals?.outmigrations_male} femaleCount={totals?.outmigrations_female}/>
                  <CardMain i18n={i18n} label={i18n['dashboard.totalDeaths']} value={totals?.deaths} icon={<RiUserUnfollowFill className="text-[25px] font-thin text- "/>} iconBg={'bg-rose-800'} genderStatus={true} maleCount={totals?.deaths_male} femaleCount={totals?.deaths_female} />
              </section>
              <section className="w-full flex flex-wrap xl:flex-nowrap gap-6 mb-6">
                <section className="w-full h-[450px] border border-outline bg-white flex flex-col justify-between items-center px-2 pt-4 rounded-xl shadow-xl">
                    <div className="w-full h-full items-center justify-center flex">
                      <div className="w-full h-[320px]">
                        <h3 className="text-title-alt font-semibold text-center pb-12">{i18n['dashboard.member.education']}</h3>
                        {loadingEducation ? <Loading/> : <DonutChart data={educationStatus}/>}
                      </div>
                      <div className="w-full h-[320px]">
                        <h3 className="text-title-alt font-semibold text-center pb-12">{i18n['dashboard.member.religion']}</h3>
                        {loadingReligion ? <Loading/> : <DonutChart data={religionStatus}/>}
                      </div>
                    </div>
                </section>
                <ChartContainer chart={loadingPyramid ? <Loading/> : <ChartPyramid i18n={i18n} data={pyramidBars} />} type="bar" label={i18n['dashboard.populationByAgeAndGender']}/>
              </section>
          </div>
          <div className="block-2">
              <h3 className="text-title-alt text-lg font-bold">{i18n['dashboard.dataCollectionStatus']}</h3>
              <section className="grid gap-5 md:w-fit xl:w-full md:grid-cols-2 xl:flex xl:justify-between xl:mx-auto max-w-[2400px]">
                  <Card i18n={i18n} label={i18n['dashboard.householdRegistration']} data={formData2} balance={23} icon={<BsHouseAdd className="text-[42px] font-thin "/>} iconBg={'bg-white'} iconColor={'text-orange-400'}/>
                  <Card i18n={i18n} label={i18n['dashboard.householdVisit']} data={formData3} balance={23} icon={<BsHouseCheck className="text-[42px] font-thin"/>} iconBg={'bg-white'} iconColor={'text-emerald-400'}/>
                  <Card i18n={i18n} label={i18n['dashboard.memberEnumeration']} data={formData4} balance={23} icon={<AiOutlineUserAdd className="text-[42px] font-thin "/>} iconBg={'bg-white'} iconColor={'text-red-400'}/>
                  <Card i18n={i18n} label={i18n['dashboard.maritalRelationship']} data={formData5} balance={23} icon={<BsPeople className="text-[42px] font-thin "/>} iconBg={'bg-white'} iconColor={'text-orange-400'}/>
              </section>
              <section className="grid gap-5 md:w-fit xl:w-full md:grid-cols-2 xl:flex xl:justify-between xl:mx-auto max-w-[2400px]">
                  <Card i18n={i18n} label={i18n['dashboard.internalInMigration']} data={formData6} balance={23} icon={<HiOutlineLogin className="text-[42px] font-thin "/>} iconBg={'bg-white'} iconColor={'text-red-400'}/>
                  <Card i18n={i18n} label={i18n['dashboard.externalInMigration']} data={formData7} balance={23} icon={<HiOutlineLogout className="text-[42px] font-thin"/>} iconBg={'bg-white'} iconColor={'text-emerald-400'}/>
                  <Card i18n={i18n} label={i18n['dashboard.outmigration']} data={formData8} balance={23} icon={<HiOutlineLogout className="text-[42px] font-thin"/>} iconBg={'bg-white'} iconColor={'text-emerald-400'}/>
                  <Card i18n={i18n} label={i18n['dashboard.deathRegistration']} data={formData9} balance={23} icon={<AiOutlineUserDelete className="text-[42px] font-thin "/>} iconBg={'bg-white'} iconColor={'text-blue-400'}/>
              </section>
              <div className={`card-wrapper ${expandCardArea == true ? 'block' : 'hidden'}`}>
                  <section className="grid gap-5 md:w-fit xl:w-full md:grid-cols-2 xl:flex xl:justify-between xl:mx-auto max-w-[2400px]">
                      <Card i18n={i18n} label={i18n['dashboard.pregnancyRegistration']} data={formData10} balance={23} icon={<MdOutlinePregnantWoman className="text-[42px] font-thin "/>} iconBg={'bg-white'} iconColor={'text-orange-400'}/>
                      <Card i18n={i18n} label={i18n['dashboard.birthRegistration']} data={formData11} balance={23} icon={<BiChild className="text-[42px] font-thin "/>} iconBg={'bg-white'} iconColor={'text-emerald-400'}/>
                      <Card i18n={i18n} label={i18n['dashboard.changeHeadHousehold']} data={formData12} balance={23} icon={<AiOutlineUserSwitch className="text-[42px] font-thin "/>} iconBg={'bg-white'} iconColor={'text-blue-400'}/>
                      <Card i18n={i18n} label={i18n['dashboard.incompleteVisit']} data={formData13} balance={23} icon={<AiOutlineUserSwitch className="text-[42px] font-thin "/>} iconBg={'bg-white'} iconColor={'text-blue-400'}/>
                  </section>
              </div>
              <button className="flex justify-center items-center my-2 mx-auto w-[50px] h-[30px] border border-outline rounded-sm bg-white shadow-xl" title={expandCardArea ? i18n['dashboard.collapse'] : i18n['dashboard.expand']} onClick={() => setExpandCardArea(!expandCardArea)}>
                  {expandCardArea ? <MdExpandLess className="text-3xl text-title"/> : <MdExpandMore className="text-3xl text-title"/>}
              </button>
              {/*Charts*/}
              {/*
              <section className="w-full flex flex-wrap xl:flex-nowrap gap-6 mb-6">
                <ChartContainer chart={<ChartPie/>} type="pie" label={"Data validation status"}/>
                <ChartContainer chart={<ChartBar/>} type="bar" controls={true} label={"Data collection status per form"}/>
              </section>
              */}
          </div>
          <div className="block-3 my-3">
              <h3 className="text-title-alt text-lg font-semibold">{i18n['dashboard.fieldworkersPerformance']}</h3>
              <section className="my-3">
                {/*<LineChartContainer/> */}
                </section>
                {/*Table*/}
                <section className="overflow-auto flex flex-col items-center my-6 min-h-[450px] bg-white">
                    {loadingFieldWorker? <Loading/> : <Table i18n={i18n} dataList={fieldworkerStatus}/>}
                </section>
          </div>
      </section>
  )
}

export default Home