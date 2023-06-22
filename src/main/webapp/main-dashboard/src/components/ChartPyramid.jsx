import { Bar, BarChart, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts"
import { dataD } from "../../public/mockData"

function ChartPyramid({i18n, data}) {
  //"#DD2567  #ced4da"

  return (
    <ResponsiveContainer width={"90%"} height={"95%"}>
          <BarChart width={400} height={450} data={data} barGap={0} layout="vertical" stackOffset="sign">
            <XAxis type="number" reversed tickFormatter={(value)=>{return value < 0 ? -value : value}}/>
            <YAxis dataKey="ageRange" type="category" reversed={true} orientation="right" angle={0}/>
            <Tooltip formatter={(value) => {return value < 0 ? -value : value}}/>
            <Legend/>
            <Bar dataKey="male" name={i18n['dashboard.males']} strokeWidth={0.1} stroke="black" barSize={8} radius={[10, 10, 0, 10]} stackId='stack' fill="#4896ea"/>
            <Bar dataKey="female" name={i18n['dashboard.females']} strokeWidth={0.1} stroke="black" barSize={8} radius={[10, 10, 0, 10]} stackId='stack' fill="#a4b5c4"/>
          </BarChart>
    </ResponsiveContainer>)
    }

export default ChartPyramid