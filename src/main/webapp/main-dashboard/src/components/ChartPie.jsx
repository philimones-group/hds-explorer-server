import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend, Label } from 'recharts';


export default function ChartPie({data, colors}) {

  const RADIAN = Math.PI / 180;
  const renderCustomizedLabel = ({ cx, cy, midAngle, innerRadius, outerRadius, percent, index }) => {
  const radius = innerRadius + (outerRadius - innerRadius) * 0.7;
  const x = cx + radius * Math.cos(-midAngle * RADIAN);
  const y = cy + radius * Math.sin(-midAngle * RADIAN);

  return (
    <text  x={x} y={y} fill="white" fontSize={10}  textAnchor={x > cx ? 'end' : 'start'} dominantBaseline="central">
      {`${(percent * 100).toFixed(0)}%`}
    </text>
  );
};

    return (
      <ResponsiveContainer width="95%" height="80%">
        <PieChart width={380} height={380}>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={renderCustomizedLabel}
            outerRadius={80}
            innerRadius={45}
            paddingAngle={2}
            //label
            fill="#8884d8"
            stroke='#455A64'
            strokeWidth={1}
            dataKey="total"
          >
            {data.map((_, index) => (
              <Cell key={`${index}`} fill={colors[index]} />
            ))}
          </Pie>
         <Legend iconType="circle" iconSize={10} wrapperStyle={{fontSize: "10px"}}/>
         <Tooltip/>
       </PieChart>
      </ResponsiveContainer>
    );
  }

