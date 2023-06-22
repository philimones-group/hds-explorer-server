import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts"
import { data } from "../../public/mockData"

function ChartBar() {
  return (
    <ResponsiveContainer width={"95%"} height={"80%"}>
          <BarChart width={400} height={400} data={data}>
            <XAxis dataKey="Date" angle={0}/>
            <YAxis/>
            <Tooltip/>
            <CartesianGrid strokeDasharray="3 3"/>
            <Bar  dataKey="HouseHold" strokeWidth={0.1} stroke="black" fill="#8bacd6"/>
          </BarChart>
    </ResponsiveContainer>)
    }

export default ChartBar