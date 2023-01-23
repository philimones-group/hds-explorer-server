use hds_explorer_db;


update member m, member f set m.father_name = f.name where m.father_id = f.id;
update member m, member o set m.mother_name = o.name where m.mother_id = o.id;
update member m, member s set m.spouse_name = s.name where m.spouse_id = s.id;
update household h, member m set h.head_name = m.name where h.head_id = m.id;