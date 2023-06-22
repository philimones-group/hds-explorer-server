/* eslint-disable react/prop-types */
import { useState } from "react"
import { dataE } from "../../public/mockData"
import ChartLine from "./ChartLine"

function LineChartContainer() {

  //State for chartData and form filter
  const [rawData, setRawData] = useState(dataE.filter((it)=> it.name == dataE[0].name))
  
  //State for form filter
  const[formData, setFormData] = useState({name:dataE[0].name, period:15})
  //Set of options for the select input - uniques names
  let setOfNames = new Set()
  dataE.map((it)=>{setOfNames.add(it.name)})

    //Onsubmit function - Filter the data based on the name and date
    const onSubmit = (e)=>{
      e.preventDefault()
      //query selecting the form inputs from the dom & retrieving its values to filter the chart data
      let nameValue = document.querySelector('#name').value
      let periodValue = document.querySelector('#period').value
      setFormData({name:nameValue, period:periodValue})
      setRawData(dataE.filter((it)=> it.name == nameValue))
    }

  return (
    <>
        <section className="w-full h-[450px] border border-outline bg-white flex flex-col justify-between items-center px-2 pt-4 rounded-xl shadow-xl">
            <h3 className="text-title-alt font-semibold pb-4">{`${formData.name} performance in last ${formData.period} days`}</h3>
            <div className="w-full h-full flex flex-col justify-between items-center">
              <ChartLine data={rawData}/>
              <div className='w-full p-4 shadow-xl rounded-lg text-sm min-w-[600px]'>
                <form className='flex justify-center items-center gap-4' onSubmit={onSubmit}>
                  <label className="font-semibold text-title-color"  htmlFor="name">Name</label>
                  <select className='text-text-color bg-white p-1.5 border' name="name" id="name">
                    {[...setOfNames].map((it) => (<option key={it} value={it}>{it}</option>))}
                  </select>
                  <label  className="font-semibold text-title-color" htmlFor="year">Period</label>
                  <input type='number' min={15} max={30} placeholder='15' className='text-text-color bg-white p-1.5 border' name="period" id="period" required />
                  <input className='block text-white bg-btn-clicked p-1 border w-[200px]' id='submit' type="submit" value="Select" />
                </form>
              </div>
            </div>
        </section>
    </> 
  )
}

export default LineChartContainer