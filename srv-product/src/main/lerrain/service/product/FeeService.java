package lerrain.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FeeService
{
	@Autowired
	FeeDao feeDao;

	public void reset()
	{
	}

	public List<FeeRate> listFeeRate(Long platformId, Long agencyId, Long productId)
	{
		return feeDao.listFeeRate(platformId, agencyId, productId);
	}

	public List<FeeRate> getFeeRate(Long platformId, Long agencyId, Long productId, String group, Map factors, Date time)
	{
		List<FeeRate> r = new ArrayList<>();

		List<FeeRate> list = feeDao.listFeeRate(platformId, agencyId, productId, group, factors);
		for (FeeRate fd : list)
		{
			if (fd.match(time))
				r.add(fd);
		}

		return r;
	}
}
