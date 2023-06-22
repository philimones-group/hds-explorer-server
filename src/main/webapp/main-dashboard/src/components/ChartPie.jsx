import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts';


export default function ChartPie() {

const data = [
  { name: 'Synchronized', value: 400 },
  { name: 'Pending', value: 300 },
  { name: 'Errors', value: 300 },
];

const COLORS = ['#34D399', '#5490DB', '#8f44f2', '#FF8042', '#d9dbe7'];
    return (
      <ResponsiveContainer width="95%" height="80%">
        <PieChart width={380} height={380}>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            labelLine={true}
            outerRadius={100}
            innerRadius={95}
            paddingAngle={5}
            fill="#8884d8"
            dataKey="value"
            label
          >
            {data.map((_, index) => (
              <Cell key={`${index}`} fill={COLORS[index]} />
            ))}
          </Pie>
          
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            outerRadius={90}
            innerRadius={60}
            paddingAngle={0}
            dataKey="value"
            fill= {COLORS[4]}  
          >
         </Pie>
         <Tooltip/>
       </PieChart>
      </ResponsiveContainer>
    );
  }

